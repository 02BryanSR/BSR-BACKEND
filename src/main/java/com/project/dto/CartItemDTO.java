package com.project.dto;

import java.time.LocalDateTime;

public class CartItemDTO {

	private Long id;
	private Integer quantity;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private Long cartId;
	private Long productId;

	public CartItemDTO() {

	}

	public CartItemDTO(Long id, Integer quantity, LocalDateTime createDate, LocalDateTime updateDate, Long cartId,
			Long productId) {

		this.id = id;
		this.quantity = quantity;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.cartId = cartId;
		this.productId = productId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public LocalDateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(LocalDateTime updateDate) {
		this.updateDate = updateDate;
	}

	public Long getCartId() {
		return cartId;
	}

	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

}
