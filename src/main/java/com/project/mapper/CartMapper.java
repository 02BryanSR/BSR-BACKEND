package com.project.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.dto.CartDTO;
import com.project.entity.CartEntity;

@Mapper(componentModel = "spring")
public interface CartMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "customer", ignore = true)
	@Mapping(target = "items", ignore = true)
	@Mapping(target = "createDate", ignore = true)
	@Mapping(target = "updateDate", ignore = true)
	CartEntity toEntity(CartDTO dto);

	@Mapping(target = "customerId", source = "customer.id")
	@Mapping(target = "itemIds", expression = "java(entity.getItems() == null ? java.util.List.of() : entity.getItems().stream().map(com.project.entity.CartItemEntity::getId).toList())")
	CartDTO toDTO(CartEntity entity);

	List<CartEntity> toListEntities(List<CartDTO> listDtos);

	List<CartDTO> toListDtos(List<CartEntity> listEntities);
}
