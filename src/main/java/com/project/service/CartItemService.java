package com.project.service;

import java.util.List;

import com.project.dto.CartItemDTO;

public interface CartItemService {

	CartItemDTO findById(Long id);

	List<CartItemDTO> findByCartId(Long cartId);

	CartItemDTO upsertCartItem(Long cartId, Long productId, Integer quantity);

	String deleteCartItem(Long id); //vaciar por item

	String deleteByCartId(Long cartId); //vaciado completo
	
}
