package com.project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderDTO {

	private Long id;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String status;
	private Integer totalAmount;
	private BigDecimal totalPrice;
	private String payMethod;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private Long customerId;
	private Long addressId;
	private List<Long> orderDetailIds;

	public OrderDTO() {

	}

	public OrderDTO(Long id, String status, Integer totalAmount, BigDecimal totalPrice, String payMethod,
			LocalDateTime createDate, LocalDateTime updateDate, Long customerId, Long addressId,
			List<Long> orderDetailIds) {
		this.id = id;
		this.status = status;
		this.totalAmount = totalAmount;
		this.totalPrice = totalPrice;
		this.payMethod = payMethod;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.customerId = customerId;
		this.addressId = addressId;
		this.orderDetailIds = orderDetailIds;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
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

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public List<Long> getOrderDetailIds() {
		return orderDetailIds;
	}

	public void setOrderDetailIds(List<Long> orderDetailIds) {
		this.orderDetailIds = orderDetailIds;
	}
}
