package com.project.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	private String address;
	private String city;
	private String country;
	private String cp;
	private String state;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime createDate;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime updateDate;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long customerId;

	public AddressDTO() {

	}

	public AddressDTO(Long id, String address, String city, String country, String cp, String state,
			LocalDateTime createDate, LocalDateTime updateDate, Long customerId) {

		this.id = id;
		this.address = address;
		this.city = city;
		this.country = country;
		this.cp = cp;
		this.state = state;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.customerId = customerId;
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

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

}
