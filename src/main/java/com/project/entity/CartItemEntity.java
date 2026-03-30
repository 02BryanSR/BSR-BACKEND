package com.project.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(columnNames = { "cart_id", "product_id" }))
public class CartItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Positive
	private Integer quantity;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id", nullable = false)
	private CartEntity cart;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private ProductEntity product;

	@Column(name = "create_date", nullable = false, updatable = false)
	private LocalDateTime createDate;

	@Column(name = "update_date", nullable = false)
	private LocalDateTime updateDate;

	@PrePersist
	protected void onCreate() {
		this.createDate = LocalDateTime.now();
		this.updateDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateDate = LocalDateTime.now();
	}

	public CartItemEntity() {

	}

	public CartItemEntity(Long id, CartEntity cart, ProductEntity product, Integer quantity, LocalDateTime createDate,
			LocalDateTime updateDate) {

		this.id = id;
		this.cart = cart;
		this.product = product;
		this.quantity = quantity;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CartEntity getCart() {
		return cart;
	}

	public void setCart(CartEntity cart) {
		this.cart = cart;
	}

	public ProductEntity getProduct() {
		return product;
	}

	public void setProduct(ProductEntity product) {
		this.product = product;
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
}
