package com.project.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponseDTO {

	private Long cartId;
	private Long customerId;
	private BigDecimal total;
	private List<CartItemSimpleDTO> items;
	private Integer totalProducts;

	public CartResponseDTO() {
	}

	public CartResponseDTO(Long cartId, Long customerId, BigDecimal total, List<CartItemSimpleDTO> items,
			Integer totalProducts) {
	
		this.cartId = cartId;
		this.customerId = customerId;
		this.total = total;
		this.items = items;
		this.totalProducts = totalProducts;
	}

	public Long getCartId() {
		return cartId;
	}

	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public List<CartItemSimpleDTO> getItems() {
		return items;
	}

	public void setItems(List<CartItemSimpleDTO> items) {
		this.items = items;
	}

	public Integer getTotalProducts() {
		return totalProducts;
	}

	public void setTotalProducts(Integer totalProducts) {
		this.totalProducts = totalProducts;
	}

	

}
