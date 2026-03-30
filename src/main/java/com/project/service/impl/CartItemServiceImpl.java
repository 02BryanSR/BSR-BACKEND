package com.project.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.dto.CartItemDTO;
import com.project.entity.CartEntity;
import com.project.entity.CartItemEntity;
import com.project.entity.ProductEntity;
import com.project.mapper.CartItemMapper;
import com.project.repository.CartItemRepository;
import com.project.repository.CartRepository;
import com.project.repository.ProductRepository;
import com.project.service.CartItemService;
import com.project.service.ProductService;

@Service
public class CartItemServiceImpl implements CartItemService {

	private CartItemRepository itemRepo;
	private CartItemMapper mapper;
	private CartRepository cartRepo;
	private ProductRepository productRepo;
	private ProductService productServ;

	public CartItemServiceImpl(CartItemRepository itemRepo, CartItemMapper mapper, CartRepository cartRepo,
			ProductRepository productRepo, ProductService productServ) {

		this.itemRepo = itemRepo;
		this.mapper = mapper;
		this.cartRepo = cartRepo;
		this.productRepo = productRepo;
		this.productServ = productServ;
	}

	@Override
	public CartItemDTO findById(Long id) {

		Optional<CartItemEntity> item = itemRepo.findById(id);
		if (item.isPresent()) {
			return mapper.toDto(item.get());
		}
		throw new RuntimeException("Item not found");
	}

	@Override
	public List<CartItemDTO> findByCartId(Long cartId) {
		 List<CartItemEntity> itemEntities = itemRepo.findByCartId(cartId);
		    return mapper.toListDto(itemEntities);
	}

	@Override
	public CartItemDTO upsertCartItem(Long cartId, Long productId, Integer quantity) {

		if (cartId == null || productId == null) {
			throw new RuntimeException("cartId and productId are required");
		}
		if (quantity == null || quantity < 0) {
			throw new RuntimeException("Invalid quantity");
		}

		CartEntity cart = cartRepo.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));

		CartItemEntity item = itemRepo.findByCartIdAndProductId(cartId, productId).orElse(null);

		if (quantity == 0) {
			if (item == null) {
				throw new RuntimeException("Item not found in cart");
			}
			itemRepo.delete(item);

			CartItemDTO deleted = new CartItemDTO();
			deleted.setId(item.getId());
			deleted.setQuantity(0);
			return deleted;
		}
		ProductEntity product = productRepo.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));

		if (!productServ.hasStock(productId, quantity)) {
			throw new RuntimeException("No hay stock suficiente");
		}

		if (item == null) {
			item = new CartItemEntity();
			item.setCart(cart);
			item.setProduct(product);
		}

		item.setQuantity(quantity);
		CartItemEntity saved = itemRepo.save(item);
		return mapper.toDto(saved);
	}

	@Override
	public String deleteCartItem(Long id) {
		if (!itemRepo.existsById(id)) {
			throw new RuntimeException("CartItem not found");
		}
		itemRepo.deleteById(id);
		return "Cart item deleted successfully";
	}

	@Override
	public String deleteByCartId(Long cartId) {

		if (!cartRepo.existsById(cartId))
			throw new RuntimeException("Cart not found");

		List<CartItemEntity> items = itemRepo.findByCartId(cartId);
		items.forEach(item -> itemRepo.delete(item));
		return "Cart successfully removed ";
	}

}
