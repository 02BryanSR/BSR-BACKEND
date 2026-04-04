package com.project.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.project.dto.ConfirmPaymentRequestDTO;
import com.project.dto.CreateOrderRequestDTO;
import com.project.dto.CreatePaymentIntentRequestDTO;
import com.project.dto.OrderDTO;
import com.project.dto.PaymentIntentResponseDTO;
import com.project.entity.AddressEntity;
import com.project.entity.CartItemEntity;
import com.project.entity.CustomerEntity;
import com.project.entity.ProductEntity;
import com.project.repository.AddressRepository;
import com.project.repository.CartItemRepository;
import com.project.repository.CustomerRepository;
import com.project.repository.OrderRepository;
import com.project.service.OrderService;
import com.project.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;

@Service
public class StripePaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StripePaymentServiceImpl.class);
    private static final String DEFAULT_CURRENCY = "eur";

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Value("${app.stripe.secret-key:}")
    private String stripeSecretKey;

    @Value("${app.stripe.webhook-secret:}")
    private String stripeWebhookSecret;

    public StripePaymentServiceImpl(
            CustomerRepository customerRepository,
            AddressRepository addressRepository,
            CartItemRepository cartItemRepository,
            OrderRepository orderRepository,
            OrderService orderService) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @PostConstruct
    void configureStripe() {
        if (stripeSecretKey != null && !stripeSecretKey.isBlank()) {
            Stripe.apiKey = stripeSecretKey.trim();
        }
    }

    @Override
    public PaymentIntentResponseDTO createPaymentIntent(CreatePaymentIntentRequestDTO dto, String email) {
        ensureStripeConfigured();
        validatePaymentIntentRequest(dto);

        CustomerEntity customer = customerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        AddressEntity address = addressRepository.findById(dto.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        validateAddressOwnership(address, customer);

        List<CartItemEntity> cartItems = cartItemRepository.findByCartCustomerId(customer.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        long amountInCents = calculateAmountInCents(cartItems);

        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("customerId", String.valueOf(customer.getId()));
            metadata.put("customerEmail", customer.getEmail());
            metadata.put("addressId", String.valueOf(address.getId()));
            metadata.put("payMethod", dto.getPayMethod().trim().toUpperCase());

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setCurrency(DEFAULT_CURRENCY)
                    .setAmount(amountInCents)
                    .addPaymentMethodType("card")
                    .putAllMetadata(metadata)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            return new PaymentIntentResponseDTO(
                    paymentIntent.getClientSecret(),
                    paymentIntent.getId(),
                    paymentIntent.getAmount(),
                    paymentIntent.getCurrency());
        } catch (StripeException ex) {
            throw new RuntimeException("No se pudo crear el PaymentIntent en Stripe", ex);
        }
    }

    @Override
    public OrderDTO confirmPayment(ConfirmPaymentRequestDTO dto, String email) {
        ensureStripeConfigured();

        String paymentIntentId = dto.getPaymentIntentId() == null ? "" : dto.getPaymentIntentId().trim();

        if (paymentIntentId.isBlank()) {
            throw new RuntimeException("paymentIntentId is required");
        }

        return orderRepository.findByPaymentReference(paymentIntentId)
                .map(existingOrder -> {
                    if (!existingOrder.getCustomer().getEmail().equalsIgnoreCase(email)) {
                        throw new RuntimeException("Access denied");
                    }
                    return orderService.findMyOrderById(existingOrder.getId(), email);
                })
                .orElseGet(() -> createOrderFromSucceededPayment(paymentIntentId, email));
    }

    @Override
    public void handleWebhook(String payload, String signatureHeader) {
        ensureStripeConfigured();

        Event event;

        try {
            if (stripeWebhookSecret != null && !stripeWebhookSecret.isBlank()) {
                event = Webhook.constructEvent(payload, signatureHeader, stripeWebhookSecret.trim());
            } else {
                event = Event.GSON.fromJson(payload, Event.class);
            }
        } catch (SignatureVerificationException ex) {
            throw new RuntimeException("Invalid Stripe webhook signature");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            String paymentIntentId = event.getDataObjectDeserializer()
                    .getObject()
                    .filter(PaymentIntent.class::isInstance)
                    .map(PaymentIntent.class::cast)
                    .map(PaymentIntent::getId)
                    .orElse(null);

            String customerEmail = event.getDataObjectDeserializer()
                    .getObject()
                    .filter(PaymentIntent.class::isInstance)
                    .map(PaymentIntent.class::cast)
                    .map(paymentIntent -> paymentIntent.getMetadata().get("customerEmail"))
                    .orElse(null);

            LOGGER.info("Stripe payment succeeded: {}", paymentIntentId);

            if (paymentIntentId != null && customerEmail != null && !customerEmail.isBlank()) {
                try {
                    if (orderRepository.findByPaymentReference(paymentIntentId).isEmpty()) {
                        createOrderFromSucceededPayment(paymentIntentId, customerEmail);
                    }
                } catch (RuntimeException ex) {
                    LOGGER.error("Unable to create order from Stripe webhook {}", paymentIntentId, ex);
                }
            }
        } else if ("payment_intent.payment_failed".equals(event.getType())) {
            LOGGER.warn("Stripe payment failed: {}", event.getId());
        } else {
            LOGGER.debug("Stripe webhook ignored: {}", event.getType());
        }
    }

    private void ensureStripeConfigured() {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new RuntimeException("Stripe secret key is not configured");
        }
    }

    private OrderDTO createOrderFromSucceededPayment(String paymentIntentId, String email) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            if (paymentIntent == null) {
                throw new RuntimeException("PaymentIntent not found");
            }

            if (!"succeeded".equalsIgnoreCase(paymentIntent.getStatus())) {
                throw new RuntimeException("Stripe payment is not confirmed yet");
            }

            String customerEmail = paymentIntent.getMetadata().get("customerEmail");
            if (customerEmail == null || !customerEmail.equalsIgnoreCase(email)) {
                throw new RuntimeException("PaymentIntent does not belong to the authenticated customer");
            }

            String addressIdValue = paymentIntent.getMetadata().get("addressId");
            String payMethod = paymentIntent.getMetadata().getOrDefault("payMethod", "CARD");

            Long addressId = parseAddressId(addressIdValue);
            CustomerEntity customer = customerRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            List<CartItemEntity> cartItems = cartItemRepository.findByCartCustomerId(customer.getId());
            long cartAmountInCents = calculateAmountInCents(cartItems);

            if (cartAmountInCents != paymentIntent.getAmount()) {
                throw new RuntimeException("The cart amount no longer matches the Stripe payment");
            }

            CreateOrderRequestDTO createOrderRequest = new CreateOrderRequestDTO();
            createOrderRequest.setAddressId(addressId);
            createOrderRequest.setPayMethod(payMethod);

            return orderService.createMyPaidOrder(
                    createOrderRequest,
                    email,
                    paymentIntent.getId(),
                    paymentIntent.getStatus());
        } catch (StripeException ex) {
            throw new RuntimeException("No se pudo verificar el pago en Stripe", ex);
        }
    }

    private Long parseAddressId(String addressIdValue) {
        if (addressIdValue == null || addressIdValue.isBlank()) {
            throw new RuntimeException("PaymentIntent metadata is missing addressId");
        }

        try {
            return Long.valueOf(addressIdValue);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("PaymentIntent addressId is invalid");
        }
    }

    private void validatePaymentIntentRequest(CreatePaymentIntentRequestDTO dto) {
        if (dto.getAddressId() == null) {
            throw new RuntimeException("addressId is required");
        }

        if (dto.getPayMethod() == null || dto.getPayMethod().isBlank()) {
            throw new RuntimeException("payMethod is required");
        }
    }

    private void validateAddressOwnership(AddressEntity address, CustomerEntity customer) {
        if (address.getCustomer() == null || !address.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Address does not belong to the authenticated customer");
        }
    }

    private long calculateAmountInCents(List<CartItemEntity> cartItems) {
        BigDecimal total = BigDecimal.ZERO;

        for (CartItemEntity cartItem : cartItems) {
            ProductEntity product = cartItem.getProduct();
            Integer quantity = cartItem.getQuantity();

            if (product == null) {
                throw new RuntimeException("Product not found in cart");
            }
            if (quantity == null || quantity <= 0) {
                throw new RuntimeException("Invalid cart quantity");
            }
            if (product.getPrice() == null) {
                throw new RuntimeException("Product price is missing: " + product.getName());
            }
            if (product.getStock() == null || product.getStock() < quantity) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        return total.movePointRight(2).longValueExact();
    }
}
