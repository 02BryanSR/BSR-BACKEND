package com.project.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ProductDTO {

    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name cannot exceed 150 characters")
    private String name;

    @NotBlank(message = "Product SKU is required")
    @Size(max = 80, message = "SKU cannot exceed 80 characters")
    private String sku;

    @NotBlank(message = "Product description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be >= 0")
    private Float price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @NotNull(message = "Category is required")
    private Long categoryId;

    public ProductDTO() {
    }

    public ProductDTO(Long id, String name, String sku, String description, Float price, Integer stock, String imageUrl,
                      LocalDateTime createDate, LocalDateTime updateDate, Long categoryId) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.categoryId = categoryId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Product name is required") @Size(max = 150, message = "Product name cannot exceed 150 characters") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Product name is required") @Size(max = 150, message = "Product name cannot exceed 150 characters") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Product SKU is required") @Size(max = 80, message = "SKU cannot exceed 80 characters") String getSku() {
        return sku;
    }

    public void setSku(
            @NotBlank(message = "Product SKU is required") @Size(max = 80, message = "SKU cannot exceed 80 characters") String sku) {
        this.sku = sku;
    }

    public @NotBlank(message = "Product description is required") @Size(max = 2000, message = "Description cannot exceed 2000 characters") String getDescription() {
        return description;
    }

    public void setDescription(
            @NotBlank(message = "Product description is required") @Size(max = 2000, message = "Description cannot exceed 2000 characters") String description) {
        this.description = description;
    }

    public @NotNull(message = "Price is required") @PositiveOrZero(message = "Price must be >= 0") Float getPrice() {
        return price;
    }

    public void setPrice(@NotNull(message = "Price is required") @PositiveOrZero(message = "Price must be >= 0") Float price) {
        this.price = price;
    }

    public @NotNull(message = "Stock is required") @Min(value = 0, message = "Stock cannot be negative") Integer getStock() {
        return stock;
    }

    public void setStock(@NotNull(message = "Stock is required") @Min(value = 0, message = "Stock cannot be negative") Integer stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public @NotNull(message = "Category is required") Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@NotNull(message = "Category is required") Long categoryId) {
        this.categoryId = categoryId;
    }
}
