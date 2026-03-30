package com.project.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.dto.CustomerDTO;
import com.project.entity.CustomerEntity;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "addressIds", expression = "java(mapAddressIds(entity))")
    @Mapping(target = "orderIds", expression = "java(mapOrderIds(entity))")
    @Mapping(target = "cartId", expression = "java(mapCartId(entity))")
    @Mapping(target = "role", expression = "java(entity.getRole() == null ? null : entity.getRole().name())")
    CustomerDTO toDto(CustomerEntity entity);

    List<CustomerDTO> toListDtos(List<CustomerEntity> listEntity);

    default List<Long> mapAddressIds(CustomerEntity entity) {
        if (entity.getAddresses() == null) {
            return List.of();
        }
        return entity.getAddresses().stream().map(a -> a.getId()).toList();
    }

    default List<Long> mapOrderIds(CustomerEntity entity) {
        if (entity.getOrders() == null) {
            return List.of();
        }
        return entity.getOrders().stream().map(o -> o.getId()).toList();
    }

    default Long mapCartId(CustomerEntity entity) {
        return entity.getCart() == null ? null : entity.getCart().getId();
    }
}
