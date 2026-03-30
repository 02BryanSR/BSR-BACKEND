package com.project.service;

import java.util.List;

import com.project.dto.ProductAdminForm;
import com.project.dto.ProductDTO;

public interface ProductService {

    List<ProductDTO> findAll();

    List<ProductDTO> findByCategoryId(Long categoryId);

    ProductDTO findById(Long id);

    List<ProductDTO> findByName(String name);

    ProductDTO createProduct(ProductAdminForm form);

    ProductDTO updateProduct(Long id, ProductAdminForm form);

    void deleteProduct(Long id);

    boolean hasStock(Long productId, Integer requiredQuantity);

    ProductDTO increaseStock(Long productId, Integer amount);

    ProductDTO decreaseStock(Long productId, Integer amount);

    Integer getProductStock(Long productId);
}
