package com.project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FavoriteDTO {

    private Long id;
    private Long customerId;
    private Long productId;
    private String productName;
    private String productSku;
    private String productImageUrl;
    private BigDecimal productPrice;
    private Integer productStock;
    private LocalDateTime createDate;

    public FavoriteDTO() {
    }

    public FavoriteDTO(Long id, Long customerId, Long productId, String productName, String productSku,
            String productImageUrl, BigDecimal productPrice, Integer productStock, LocalDateTime createDate) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.productImageUrl = productImageUrl;
        this.productPrice = productPrice;
        this.productStock = productStock;
        this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
