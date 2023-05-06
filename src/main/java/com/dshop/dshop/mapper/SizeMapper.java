package com.dshop.dshop.mapper;

import com.dshop.dshop.models.Size;
import com.dshop.dshop.models.dtos.SizeDTO;
import com.dshop.dshop.models.request.SizeRequest;
import com.dshop.dshop.models.response.SizeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper(uses = ProductMapper.class)
public interface SizeMapper {
    // mapper one model to dto
    SizeDTO mapModelToDTO(Size Size);

    // mapper list model to dto
    List<SizeDTO> mapModelToDTOs(List<Size> Sizes);

    // mapper one dto to model
    Size mapDTOToModel(SizeDTO SizeDTO);

    // mapper list dto to model
    List<Size> mapDTOToModels(List<SizeDTO> SizeDTOS);

    SizeResponse mapModelToResponse(Size size);

    @Mapping(target = "color.id", source = "colorId")
    Size mapRequestedToModel(SizeRequest sizeRequest);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateModel(@MappingTarget Size Size, RequestedSize requestedSize);

}
