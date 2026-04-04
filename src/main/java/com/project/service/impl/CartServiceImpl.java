package com.project.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.dto.CartItemSimpleDTO;
import com.project.dto.CartResponseDTO;
import com.project.entity.CartEntity;
import com.project.entity.CartItemEntity;
import com.project.entity.CustomerEntity;
import com.project.entity.ProductEntity;
import com.project.repository.CartRepository;
import com.project.repository.CustomerRepository;
import com.project.repository.ProductRepository;
import com.project.service.CartService;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDTO getMyCart(String email) {
        Long customerId = getCustomerByEmail(email).getId();
        return getCart(customerId);
    }

    @Override
    public void addItemToMyCart(String email, Long productId, Integer quantity, String size) {
        Long customerId = getCustomerByEmail(email).getId();
        addItem(customerId, productId, quantity, size);
    }

    @Override
    public void updateMyItemQuantity(String email, Long itemId, Integer quantity) {
        Long customerId = getCustomerByEmail(email).getId();
        updateItemQuantity(customerId, itemId, quantity);
    }

    @Override
    public void removeMyItem(String email, Long itemId) {
        Long customerId = getCustomerByEmail(email).getId();
        removeItem(customerId, itemId);
    }

    @Override
    public void clearMyCart(String email) {
        Long customerId = getCustomerByEmail(email).getId();
        clear(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDTO getCart(Long customerId) {
        CustomerEntity customer = getCustomerById(customerId);
        CartEntity cart = getOrCreateCart(customer);

        return toResponse(cart);
    }

    @Override
    public void addItem(Long customerId, Long productId, Integer quantity, String size) {
        validateQuantity(quantity);

        String normalizedSize = normalizeSize(size);
        validateSize(normalizedSize);

        CustomerEntity customer = getCustomerById(customerId);
        ProductEntity product = getProductById(productId);
        CartEntity cart = getOrCreateCart(customer);

        validateStock(product, quantity);

        Optional<CartItemEntity> existingItem = cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId)
                        && normalizeSize(item.getSize()).equals(normalizedSize))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItemEntity item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            validateStock(product, newQuantity);

            item.setQuantity(newQuantity);
        } else {
            CartItemEntity newItem = new CartItemEntity();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setSize(normalizedSize);

            if (cart.getItems() == null) {
                cart.setItems(new ArrayList<>());
            }

            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long customerId, Long itemId, Integer quantity) {
        validateQuantity(quantity);

        CartEntity cart = getOrCreateCart(getCustomerById(customerId));
        CartItemEntity item = cart.getItems()
                .stream()
                .filter(candidate -> candidate.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("La linea del carrito no existe"));

        validateStock(item.getProduct(), quantity);

        item.setQuantity(quantity);
        cartRepository.save(cart);
    }

    @Override
    public void removeItem(Long customerId, Long itemId) {
        CartEntity cart = getOrCreateCart(getCustomerById(customerId));

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));

        if (!removed) {
            throw new RuntimeException("La linea del carrito no existe");
        }

        cartRepository.save(cart);
    }

    @Override
    public void clear(Long customerId) {
        CartEntity cart = getOrCreateCart(getCustomerById(customerId));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CustomerEntity getCustomerByEmail(String email) {
        return customerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    private CustomerEntity getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    private ProductEntity getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    private CartEntity getOrCreateCart(CustomerEntity customer) {
        return cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    CartEntity cart = new CartEntity();
                    cart.setCustomer(customer);
                    cart.setItems(new ArrayList<>());
                    return cartRepository.save(cart);
                });
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor que 0");
        }
    }

    private void validateStock(ProductEntity product, Integer quantity) {
        if (product.getStock() == null || quantity > product.getStock()) {
            throw new RuntimeException("No hay stock suficiente");
        }
    }

    private void validateSize(String size) {
        if (size != null && size.length() > 40) {
            throw new RuntimeException("La talla no puede superar los 40 caracteres");
        }
    }

    private String normalizeSize(String size) {
        return size == null ? "" : size.trim();
    }

    private String toResponseSize(String size) {
        String normalizedSize = normalizeSize(size);
        return normalizedSize.isEmpty() ? null : normalizedSize;
    }

    private CartResponseDTO toResponse(CartEntity cart) {

        CartResponseDTO dto = new CartResponseDTO();

        dto.setCartId(cart.getId());
        dto.setCustomerId(cart.getCustomer().getId());

        BigDecimal total = BigDecimal.ZERO;
        int totalProducts = 0;

        List<CartItemSimpleDTO> itemsDto = new ArrayList<>();

        if (cart.getItems() != null) {
            for (CartItemEntity item : cart.getItems()) {

                CartItemSimpleDTO itemDto = new CartItemSimpleDTO();

                BigDecimal price = item.getProduct().getPrice();
                Integer quantity = item.getQuantity();

                BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));

                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setProductName(item.getProduct().getName());
                itemDto.setSize(toResponseSize(item.getSize()));
                itemDto.setPrice(price);
                itemDto.setQuantity(quantity);
                itemDto.setSubtotal(subtotal);

                itemsDto.add(itemDto);

                total = total.add(subtotal);
                totalProducts += quantity;
            }
        }

        dto.setItems(itemsDto);
        dto.setTotal(total);
        dto.setTotalProducts(totalProducts);

        return dto;
    }
}
