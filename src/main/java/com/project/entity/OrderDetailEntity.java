package com.project.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "order_details", uniqueConstraints = @UniqueConstraint(columnNames = { "order_id", "product_id" }))
public class OrderDetailEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Positive
	private Integer quantity;

	@NotNull
	@Positive
	@Column(name = "price_unit", nullable = false)
	private BigDecimal priceUnit;

	@Column(name = "create_date", nullable = false, updatable = false)
	private LocalDateTime createDate;

	@Column(name = "update_date", nullable = false)
	private LocalDateTime updateDate;

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private OrderEntity order;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private ProductEntity product;

	@PrePersist
	protected void onCreate() {
		this.createDate = LocalDateTime.now();
		this.updateDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateDate = LocalDateTime.now();

	}

	public OrderDetailEntity() {

	}

	public OrderDetailEntity(Long id, @NotNull @Positive Integer quantity, @NotNull @Positive BigDecimal priceUnit,
			LocalDateTime createDate, LocalDateTime updateDate, OrderEntity order, ProductEntity product) {

		this.id = id;
		this.quantity = quantity;
		this.priceUnit = priceUnit;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.order = order;
		this.product = product;
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

	public BigDecimal getPriceUnit() {
		return priceUnit;
	}

	public void setPriceUnit(BigDecimal priceUnit) {
		this.priceUnit = priceUnit;
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

	public OrderEntity getOrder() {
		return order;
	}

	public void setOrder(OrderEntity order) {
		this.order = order;
	}

	public ProductEntity getProduct() {
		return product;
	}

	public void setProduct(ProductEntity product) {
		this.product = product;
	}

}
