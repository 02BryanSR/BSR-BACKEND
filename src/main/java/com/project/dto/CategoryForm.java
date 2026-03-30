package com.project.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryForm {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Category description is required")
    @Size(max = 500, message = "Category description cannot exceed 500 characters")
    private String description;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    private MultipartFile image;

    public CategoryForm() {
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

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
