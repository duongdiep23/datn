package com.dshop.dshop.mapper;

import com.dshop.dshop.models.dtos.ImageDTO;
import com.dshop.dshop.models.request.ImageRequest;
import com.dshop.dshop.models.Image;
import com.dshop.dshop.models.response.ImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ImageMapper {

	// map one model to dto
	@Mapping(target = "url", source = "image.url")
	ImageDTO mapModelToDTO(Image image);

	// map list model to dto
	@Mapping(target = "url", source = "image.url")
	List<ImageDTO> mapModelToDTOs(List<Image> images);

	// mapper one dto to model
	Image mapDTOToModel(ImageDTO imageDTO);

	// map list dto to model
	List<Image> mapDTOToModels(List<ImageDTO> imageDTOS);
	ImageResponse mapModelToResponse(Image image);
	@Mapping(target = "product.id", source = "productId")
	Image mapRequestedToModel(ImageRequest imageRequest);
}
