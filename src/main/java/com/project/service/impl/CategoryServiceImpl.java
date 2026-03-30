package com.project.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.dto.CategoryForm;
import com.project.dto.CategoryDTO;
import com.project.entity.CategoryEntity;
import com.project.mapper.CategoryMapper;
import com.project.repository.CategoryRepository;
import com.project.service.CategoryService;
import com.project.service.StorageService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper mapper;
    private final CategoryRepository repo;
    private final StorageService storageService;

    public CategoryServiceImpl(CategoryMapper mapper, CategoryRepository repo, StorageService storageService) {
        this.mapper = mapper;
        this.repo = repo;
        this.storageService = storageService;
    }

    @Override
    public List<CategoryDTO> findAll() {
        List<CategoryEntity> listCategories = repo.findAll();
        return mapper.toListDtos(listCategories);
    }

    @Override
    public CategoryDTO findById(Long id) {
        CategoryEntity category = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        return mapper.toDto(category);
    }

    @Override
    public CategoryDTO createCategory(CategoryForm form) {
        validateCreate(form);

        String normalizedName = form.getName().trim();

        if (repo.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("A category with that name already exists");
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setName(form.getName().trim());
        dto.setDescription(form.getDescription().trim());
        dto.setImageUrl(form.getImageUrl());

        CategoryEntity category = mapper.toEntity(dto);
        category.setName(normalizedName);
        category.setDescription(form.getDescription().trim());
        category.setImageUrl(resolveImageUrl(form.getImageUrl(), form.getImage(), null));

        CategoryEntity saved = repo.save(category);
        return mapper.toDto(saved);
    }

    @Override
    public CategoryDTO updateCategory(CategoryForm form, Long id) {
        CategoryEntity categoryFound = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        if (form.getName() != null && !form.getName().isBlank()) {
            String newName = form.getName().trim();

            if (!categoryFound.getName().equalsIgnoreCase(newName)
                    && repo.existsByNameIgnoreCase(newName)) {
                throw new IllegalArgumentException("A category with that name already exists");
            }

            categoryFound.setName(newName);
        }

        if (form.getDescription() != null && !form.getDescription().isBlank()) {
            categoryFound.setDescription(form.getDescription().trim());
        }

        categoryFound.setImageUrl(resolveImageUrl(form.getImageUrl(), form.getImage(), categoryFound.getImageUrl()));

        CategoryEntity saved = repo.save(categoryFound);
        return mapper.toDto(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id: " + id);
        }
        repo.deleteById(id);
    }

    private void validateCreate(CategoryForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Category data is required");
        }
        if (form.getName() == null || form.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (form.getDescription() == null || form.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
    }

    private String resolveImageUrl(String requestedImageUrl, org.springframework.web.multipart.MultipartFile image,
            String currentImageUrl) {
        if (image != null && !image.isEmpty()) {
            storageService.deleteIfManaged(currentImageUrl);
            return storageService.storeCategoryImage(image);
        }

        if (requestedImageUrl != null && !requestedImageUrl.isBlank()) {
            String normalized = requestedImageUrl.trim();

            if (!normalized.equals(currentImageUrl)) {
                storageService.deleteIfManaged(currentImageUrl);
            }
            return normalized;
        }

        return currentImageUrl;
    }
}
