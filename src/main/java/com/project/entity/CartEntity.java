package com.project.entity;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.*;

@Entity
@Table(name = "carts")
public class CartEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne()
	@JoinColumn(name = "customer_id", nullable = false, unique = true)
	private CustomerEntity customer;

	@Column(name = "create_date", nullable = false, updatable = false)
	private LocalDateTime createDate;

	@Column(name = "update_date", nullable = false)
	private LocalDateTime updateDate;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItemEntity> items = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.createDate = LocalDateTime.now();
		this.updateDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateDate = LocalDateTime.now();
	}

	public CartEntity() {
	}

	public CartEntity(Long id, CustomerEntity customer, LocalDateTime createDate, LocalDateTime updateDate,
			List<CartItemEntity> items) {

		this.id = id;
		this.customer = customer;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.items = items;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CustomerEntity getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerEntity customer) {
		this.customer = customer;
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

	public List<CartItemEntity> getItems() {
		return items;
	}

	public void setItems(List<CartItemEntity> items) {
		this.items = items;
	}

}
