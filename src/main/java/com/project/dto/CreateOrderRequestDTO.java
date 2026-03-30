package com.project.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateOrderRequestDTO {

    @NotNull(message = "addressId is required")
    private Long addressId;

    @NotBlank(message = "payMethod is required")
    private String payMethod;

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public String getPayMethod() { return payMethod; }
    public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
}
