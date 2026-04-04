package com.project.service;

import com.project.dto.CartResponseDTO;

public interface CartService {

    // USER / ADMIN autenticado
    CartResponseDTO getMyCart(String email);

    void addItemToMyCart(String email, Long productId, Integer quantity, String size);

    void updateMyItemQuantity(String email, Long itemId, Integer quantity);

    void removeMyItem(String email, Long itemId);

    void clearMyCart(String email);

    // SOLO ADMIN o uso interno
    CartResponseDTO getCart(Long customerId);

    void addItem(Long customerId, Long productId, Integer quantity, String size);

    void updateItemQuantity(Long customerId, Long itemId, Integer quantity);

    void removeItem(Long customerId, Long itemId);

    void clear(Long customerId);
}
