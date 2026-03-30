package com.project.service;

import java.util.List;

import com.project.dto.CategoryForm;
import com.project.dto.CategoryDTO;

public interface CategoryService {

    List<CategoryDTO> findAll();

    CategoryDTO findById(Long id);

    CategoryDTO createCategory(CategoryForm form);

    CategoryDTO updateCategory(CategoryForm form, Long id);

    void deleteCategory(Long id);
}
