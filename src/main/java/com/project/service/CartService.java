package com.project.service;

import com.project.dto.CartResponseDTO;

public interface CartService {

    // USER / ADMIN autenticado
    CartResponseDTO getMyCart(String email);

    void addItemToMyCart(String email, Long productId, Integer quantity);

    void updateMyItemQuantity(String email, Long productId, Integer quantity);

    void removeMyItem(String email, Long productId);

    void clearMyCart(String email);

    // SOLO ADMIN o uso interno
    CartResponseDTO getCart(Long customerId);

    void addItem(Long customerId, Long productId, Integer quantity);

    void updateItemQuantity(Long customerId, Long productId, Integer quantity);

    void removeItem(Long customerId, Long productId);

    void clear(Long customerId);
}