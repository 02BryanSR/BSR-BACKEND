package com.project.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.dto.CartItemDTO;
import com.project.entity.CartItemEntity;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "cart", ignore = true)
	@Mapping(target = "product", ignore = true)
	@Mapping(target = "createDate", ignore = true)
	@Mapping(target = "updateDate", ignore = true)
	CartItemEntity toEntity(CartItemDTO dto);

	@Mapping(target = "cartId", source = "cart.id")
	@Mapping(target = "productId", source = "product.id")
	CartItemDTO toDto(CartItemEntity entity);

	List<CartItemEntity> toListEntity(List<CartItemDTO> listDto);

	List<CartItemDTO> toListDto(List<CartItemEntity> listEntity);
}
