package com.project.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.project.dto.CreateOrderRequestDTO;
import com.project.dto.OrderDTO;
import com.project.entity.AddressEntity;
import com.project.entity.CartItemEntity;
import com.project.entity.CustomerEntity;
import com.project.entity.OrderDetailEntity;
import com.project.entity.OrderEntity;
import com.project.entity.ProductEntity;
import com.project.entity.enums.OrderStatus;
import com.project.mapper.OrderMapper;
import com.project.repository.AddressRepository;
import com.project.repository.CartItemRepository;
import com.project.repository.CustomerRepository;
import com.project.repository.OrderDetailRepository;
import com.project.repository.OrderRepository;
import com.project.service.OrderService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repo;
    private final OrderMapper mapper;
    private final CustomerRepository customerRepo;
    private final AddressRepository addressRepo;
    private final CartItemRepository cartItemRepo;
    private final OrderDetailRepository orderDetailRepo;

    public OrderServiceImpl(
            OrderRepository repo,
            OrderMapper mapper,
            CustomerRepository customerRepo,
            AddressRepository addressRepo,
            CartItemRepository cartItemRepo,
            OrderDetailRepository orderDetailRepo) {
        this.repo = repo;
        this.mapper = mapper;
        this.customerRepo = customerRepo;
        this.addressRepo = addressRepo;
        this.cartItemRepo = cartItemRepo;
        this.orderDetailRepo = orderDetailRepo;
    }

    @Override
    public List<OrderDTO> findAll() {
        return mapper.toListDtos(repo.findAll());
    }

    @Override
    public List<OrderDTO> findByCustomerId(Long customerId) {
        return mapper.toListDtos(repo.findByCustomerId(customerId));
    }

    @Override
    public List<OrderDTO> findByStatus(OrderStatus status) {
        return mapper.toListDtos(repo.findByStatus(status));
    }

    @Override
    public OrderDTO findById(Long id) {
        OrderEntity order = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("The order with id: " + id + " does not exist"));
        return mapper.toDto(order);
    }

    @Override
    public OrderDTO createOrder(OrderDTO dto) {
        if (dto.getCustomerId() == null) {
            throw new RuntimeException("customerId is required");
        }

        validateOrderRequest(dto);

        CustomerEntity customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        AddressEntity address = addressRepo.findById(dto.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        validateAddressOwnership(address, customer);

        return buildOrderFromCart(customer, address, dto.getPayMethod());
    }

    @Override
    public OrderDTO updateStatus(Long orderId, OrderStatus status) {
        if (status == null) {
            throw new RuntimeException("Status is required");
        }

        OrderEntity orderFound = repo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderFound.setStatus(status);

        OrderEntity saved = repo.save(orderFound);
        return mapper.toDto(saved);
    }

    @Override
    public OrderDTO findMyOrderById(Long orderId, String email) {
        OrderEntity order = repo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Access denied");
        }

        return mapper.toDto(order);
    }

    @Override
    public List<OrderDTO> findMyOrders(String email) {
        CustomerEntity customer = customerRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<OrderEntity> orders = repo.findByCustomerId(customer.getId());
        return mapper.toListDtos(orders);
    }

    @Override
    public OrderDTO createMyOrder(CreateOrderRequestDTO dto, String email) {
        validateOrderRequest(dto);

        CustomerEntity customer = customerRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        AddressEntity address = addressRepo.findById(dto.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        validateAddressOwnership(address, customer);

        return buildOrderFromCart(customer, address, dto.getPayMethod());
    }

    private void validateOrderRequest(OrderDTO dto) {
        if (dto.getAddressId() == null) {
            throw new RuntimeException("addressId is required");
        }
        if (dto.getPayMethod() == null || dto.getPayMethod().isBlank()) {
            throw new RuntimeException("payMethod is required");
        }
    }

    private void validateOrderRequest(CreateOrderRequestDTO dto) {
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

    private OrderDTO buildOrderFromCart(CustomerEntity customer, AddressEntity address, String payMethod) {
        List<CartItemEntity> cartItems = cartItemRepo.findByCartCustomerId(customer.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        OrderEntity order = new OrderEntity();
        order.setCustomer(customer);
        order.setAddress(address);
        order.setPayMethod(payMethod.trim());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(0);
        order.setTotalPrice(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));

        OrderEntity savedOrder = repo.save(order);

        int totalAmount = 0;
        BigDecimal totalPrice = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        List<OrderDetailEntity> savedDetails = new ArrayList<>();

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

            OrderDetailEntity detail = new OrderDetailEntity();
            detail.setOrder(savedOrder);
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setPriceUnit(product.getPrice());

            savedDetails.add(orderDetailRepo.save(detail));

            product.setStock(product.getStock() - quantity);

            totalAmount += quantity;
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        savedOrder.setOrderDetails(savedDetails);
        savedOrder.setTotalAmount(totalAmount);
        savedOrder.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));

        OrderEntity updatedOrder = repo.save(savedOrder);

        cartItemRepo.deleteAll(cartItems);

        return mapper.toDto(updatedOrder);
    }
}
