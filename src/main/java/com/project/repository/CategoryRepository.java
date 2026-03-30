package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.entity.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    boolean existsByNameIgnoreCase(String name);
}
