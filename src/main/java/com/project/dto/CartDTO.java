package com.project.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CartDTO {

	private Long id;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private Long customerId;
	private List<Long> itemIds;

	public CartDTO() {

	}

	public CartDTO(Long id, LocalDateTime createDate, LocalDateTime updateDate, Long customerId, List<Long> itemIds) {

		this.id = id;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.customerId = customerId;
		this.itemIds = itemIds;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<Long> getItemIds() {
		return itemIds;
	}

	public void setItemIds(List<Long> itemIds) {
		this.itemIds = itemIds;
	}

}
