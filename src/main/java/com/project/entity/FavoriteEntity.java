package com.project.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "product_id" })
)
public class FavoriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }

    public FavoriteEntity() {
    }

    public FavoriteEntity(Long id, CustomerEntity customer, ProductEntity product, LocalDateTime createDate) {
        this.id = id;
        this.customer = customer;
        this.product = product;
        this.createDate = createDate;
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

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
