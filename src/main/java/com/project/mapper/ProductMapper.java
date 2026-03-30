package com.project.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.project.dto.ProductDTO;
import com.project.entity.ProductEntity;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "orderDetails", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    ProductEntity toEntity(ProductDTO dto);

    @Mapping(target = "categoryId", source = "category.id")
    ProductDTO toDto(ProductEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "orderDetails", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    void updateEntityFromDto(ProductDTO dto, @MappingTarget ProductEntity entity);

    List<ProductEntity> toListEntities(List<ProductDTO> listDtos);

    List<ProductDTO> toListDtos(List<ProductEntity> listEntities);
}
