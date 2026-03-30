package com.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByCategory_Id(Long categoryId);

    boolean existsByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    Optional<ProductEntity> findTopByOrderByIdDesc();
}
