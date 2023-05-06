package com.dshop.dshop.mapper;

import com.dshop.dshop.models.dtos.RoleDTO;
import com.dshop.dshop.models.Role;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface RoleMapper {
	RoleDTO mapModelToDTO(Role role);

	// mapper list model to dto
	List<RoleDTO> mapModelToDTOs(List<Role> roles);

	// mapper one dto to model
	Role mapDTOToModel(RoleDTO roleDTO);

	// mapper list dto to model
	List<Role> mapDTOToModels(List<RoleDTO> roleDTOS);
}
