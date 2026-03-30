package com.project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDetailDTO {

	private Long id;
	private Integer quantity;
	private BigDecimal priceUnit;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private Long orderId;
	private Long productId;

	public OrderDetailDTO() {

	}

	public OrderDetailDTO(Long id, Integer quantity, BigDecimal priceUnit, LocalDateTime createDate,
			LocalDateTime updateDate, Long orderId, Long productId) {

		this.id = id;
		this.quantity = quantity;
		this.priceUnit = priceUnit;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.orderId = orderId;
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

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

}
