package com.project.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.dto.CategoryDTO;
import com.project.entity.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "productIds", expression = "java(mapProductIds(entity))")
    CategoryDTO toDto(CategoryEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    CategoryEntity toEntity(CategoryDTO dto);

    List<CategoryDTO> toListDtos(List<CategoryEntity> listEntities);

    List<CategoryEntity> toListEntities(List<CategoryDTO> listDtos);

    default List<Long> mapProductIds(CategoryEntity entity) {
        if (entity.getProducts() == null) {
            return List.of();
        }
        return entity.getProducts().stream().map(ProductEntity -> ProductEntity.getId()).toList();
    }
}
