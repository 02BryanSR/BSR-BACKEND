package com.project.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.entity.enums.Role;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "customers")
public class CustomerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false)
	private String name;

	@NotBlank
	@Column(nullable = false, name = "last_name")
	private String lastName;

	@Email(message = "The email address is invalid.")
	@Size(max = 100)
	private String email;

	private Long number;

	@JsonIgnore
	@NotBlank
	@Size(min = 6, max = 60)
	@Column(nullable = false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role = Role.USER;

	@Column(nullable = false)
	private boolean enabled;


	@Column(name = "create_date", nullable = false, updatable = false)
	private LocalDateTime createDate;

	@Column(name = "update_date", nullable = false)
	private LocalDateTime updateDate;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AddressEntity> addresses;

	@OneToMany(mappedBy = "customer")
	private List<OrderEntity> orders;

	@OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
	private CartEntity cart;

	@PrePersist
	protected void onCreate() {
		this.createDate = LocalDateTime.now();
		this.updateDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateDate = LocalDateTime.now();
	}

	public CustomerEntity() {
	}

	

	public CustomerEntity(Long id, @NotBlank String name, @NotBlank String lastName,
			@Email(message = "The email address is invalid.") @Size(max = 60) String email, Long number,
			@NotBlank @Size(min = 6, max = 16) String password, Role role, boolean enabled, LocalDateTime createDate,
			LocalDateTime updateDate, List<AddressEntity> addresses, List<OrderEntity> orders, CartEntity cart) {
		
		this.id = id;
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.number = number;
		this.password = password;
		this.role = role;
		this.enabled = enabled;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.addresses = addresses;
		this.orders = orders;
		this.cart = cart;
	}

	public List<OrderEntity> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderEntity> orders) {
		this.orders = orders;
	}

	public CartEntity getCart() {
		return cart;
	}

	public void setCart(CartEntity cart) {
		this.cart = cart;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<AddressEntity> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressEntity> addresses) {
		this.addresses = addresses;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	

}
