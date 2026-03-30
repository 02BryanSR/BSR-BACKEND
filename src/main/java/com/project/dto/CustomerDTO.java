package com.project.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerDTO {

    private Long id;
    private String name;
    private String lastName;
    private String email;
    private Long number;
    private String role;
    private Boolean enabled;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private List<Long> addressIds;
    private List<Long> orderIds;
    private Long cartId;

    public CustomerDTO() {
    }

    public CustomerDTO(Long id, String name, String lastName, String email, Long number, String role, Boolean enabled,
                       LocalDateTime createDate, LocalDateTime updateDate,
                       List<Long> addressIds, List<Long> orderIds, Long cartId) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.number = number;
        this.role = role;
        this.enabled = enabled;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.addressIds = addressIds;
        this.orderIds = orderIds;
        this.cartId = cartId;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public List<Long> getAddressIds() {
        return addressIds;
    }

    public void setAddressIds(List<Long> addressIds) {
        this.addressIds = addressIds;
    }

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
}
