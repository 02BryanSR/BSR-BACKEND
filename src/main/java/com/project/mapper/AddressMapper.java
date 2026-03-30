package com.project.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.dto.AddressDTO;
import com.project.entity.AddressEntity;

@Mapper(componentModel = "spring")
public interface AddressMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "customer", ignore = true)
	@Mapping(target = "createDate", ignore = true)
	@Mapping(target = "updateDate", ignore = true)
	AddressEntity toEntity(AddressDTO dto);

	@Mapping(target = "customerId", expression = "java(entity.getCustomer() != null ? entity.getCustomer().getId() : null)")
	AddressDTO toDto(AddressEntity entity);

	List<AddressEntity> toListEntity(List<AddressDTO> listDto);

	List<AddressDTO> toListDto(List<AddressEntity> listEntity);
}
