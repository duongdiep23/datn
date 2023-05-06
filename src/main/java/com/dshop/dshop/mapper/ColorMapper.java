package com.dshop.dshop.mapper;

import com.dshop.dshop.models.Color;
import com.dshop.dshop.models.dtos.ColorDTO;
import com.dshop.dshop.models.request.ColorRequest;
import com.dshop.dshop.models.response.ColorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(uses = ProductMapper.class)
public interface ColorMapper {
    // mapper one model to dto
    ColorDTO mapModelToDTO(Color Color);

    // mapper list model to dto
    List<ColorDTO> mapModelToDTOs(List<Color> Colors);

    // mapper one dto to model
    Color mapDTOToModel(ColorDTO ColorDTO);

    // mapper list dto to model
    List<Color> mapDTOToModels(List<ColorDTO> ColorDTOS);

    @Mapping(target = "sizeResponses", source = "sizes")
    ColorResponse mapModelToResponse(Color color);

    @Mapping(target = "product.id", source = "productId")
    Color mapRequestedToModel(ColorRequest colorRequest);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateModel(@MappingTarget Color Color, RequestedColor requestedColor);

}
