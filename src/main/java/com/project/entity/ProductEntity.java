package com.project.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, unique = true, length = 80)
    private String sku;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    private String description;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    private Integer stock;

    @Size(max = 500)
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @OneToMany(mappedBy = "product")
    private List<OrderDetailEntity> orderDetails;

    @OneToMany(mappedBy = "product")
    private List<CartItemEntity> cartItems;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }

    public ProductEntity() {
    }

    public ProductEntity(Long id, String name, String sku, String description, BigDecimal price, Integer stock, String imageUrl,
                         LocalDateTime createDate, LocalDateTime updateDate, CategoryEntity category,
                         List<OrderDetailEntity> orderDetails, List<CartItemEntity> cartItems) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.category = category;
        this.orderDetails = orderDetails;
        this.cartItems = cartItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public @NotBlank @Size(max = 80) String getSku() {
        return sku;
    }

    public void setSku(@NotBlank @Size(max = 80) String sku) {
        this.sku = sku;
    }

    public @NotBlank @Size(max = 2000) String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank @Size(max = 2000) String description) {
        this.description = description;
    }

    public @NotNull BigDecimal getPrice() {
        return price;
    }

    public void setPrice(@NotNull BigDecimal price) {
        this.price = price;
    }

    public @NotNull Integer getStock() {
        return stock;
    }

    public void setStock(@NotNull Integer stock) {
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

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public List<OrderDetailEntity> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailEntity> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<CartItemEntity> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemEntity> cartItems) {
        this.cartItems = cartItems;
    }
}
