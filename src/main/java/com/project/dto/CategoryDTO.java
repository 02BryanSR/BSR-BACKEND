package com.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @NotBlank(message = "La descripcion es obligatoria")
    @Size(max = 500, message = "La descripcion no puede superar los 500 caracteres")
    private String description;

    @Size(max = 500, message = "La URL de imagen no puede superar los 500 caracteres")
    private String imageUrl;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private List<Long> productIds;

    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String name, String description, String imageUrl, LocalDateTime createDate,
                       LocalDateTime updateDate, List<Long> productIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.productIds = productIds;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }
}
