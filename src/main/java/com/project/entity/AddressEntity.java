package com.project.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses", indexes = @Index(name = "idx_addresses_customer_id", columnList = "customer_id"))
public class AddressEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String address;

	@Column(nullable = false, length = 50)
	private String city;

	@Column(nullable = false, length = 50)
	private String country;

	@Column(nullable = false, length = 10)
	private String cp;

	@Column(nullable = false, length = 25)
	private String state;

	@Column(name = "create_date", nullable = false, updatable = false)
	private LocalDateTime createDate;

	@Column(name = "update_date", nullable = false)
	private LocalDateTime updateDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private CustomerEntity customer;

	@PrePersist
	protected void onCreate() {
		this.createDate = LocalDateTime.now();
		this.updateDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateDate = LocalDateTime.now();
	}

	public AddressEntity() {

	}

	public AddressEntity(Long id, String address, String city, String country, String cp, String state,
			LocalDateTime createDate, LocalDateTime updateDate, CustomerEntity customer) {
		super();
		this.id = id;
		this.address = address;
		this.city = city;
		this.country = country;
		this.cp = cp;
		this.state = state;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.customer = customer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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

}
