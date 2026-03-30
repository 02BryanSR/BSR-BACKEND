package com.project.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.project.entity.enums.OrderStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "orders")
public class OrderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	@Column(name= "total_amount" ,nullable = false)
	@PositiveOrZero
	private Integer totalAmount;

	@Column(name= "total_price" ,nullable = false)
	@PositiveOrZero 
	private BigDecimal totalPrice;

	@Column(name= "pay_method",nullable = false)
	private String payMethod;

	@Column(name = "create_date", nullable = false, updatable = false)
	private LocalDateTime createDate;

	@Column(name = "update_date", nullable = false)
	private LocalDateTime updateDate;

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private CustomerEntity customer;

	@ManyToOne
	@JoinColumn(name = "address_id", nullable = false)
	private AddressEntity address;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderDetailEntity> orderDetails;

	@PrePersist
	protected void onCreate() {
		this.createDate = LocalDateTime.now();
		this.updateDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateDate = LocalDateTime.now();
	}

	public OrderEntity() {

	}

	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Integer getTotalAmount() {
		return totalAmount;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
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

	public CustomerEntity getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerEntity customer) {
		this.customer = customer;
	}

	public AddressEntity getAddress() {
		return address;
	}

	public void setAddress(AddressEntity address) {
		this.address = address;
	}

	public List<OrderDetailEntity> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<OrderDetailEntity> orderDetails) {
		this.orderDetails = orderDetails;
	}

}
