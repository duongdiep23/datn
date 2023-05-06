package com.dshop.dshop.mapper;

import com.dshop.dshop.models.Color;
import com.dshop.dshop.models.Size;
import com.dshop.dshop.models.dtos.ProductDTO;
import com.dshop.dshop.models.request.ProductRequest;
import com.dshop.dshop.models.Image;
import com.dshop.dshop.models.Product;
import com.dshop.dshop.models.response.*;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ProductMapper {
	// mapper one model to dto
	@Mapping(target = "categoryTitle", source = "category.title")
	@Mapping(target = "authorFullname", source = "user.fullname")
	ProductDTO mapModelToDTO(Product product);

	// mapper list model to dto
	@Mapping(target = "categoryTitle", source = "category.title")
	@Mapping(target = "authorFullname", source = "user.fullname")
	List<ProductDTO> mapModelToDTOs(List<Product> products);

	// mapper one dto to model
	Product mapDTOToModel(ProductDTO productDTO);

	// mapper list dto to model
	List<Product> mapDTOToModels(List<ProductDTO> productDTOS);

	//map danh sách ProductResponse với danh sách Product
	@Mapping(target = "colorResponses", source = "colors")
	@Mapping(target = "imageResponses", source = "images")
	@Mapping(target = "categoryId",source = "category.id")
	ProductResponse toProductResponse(Product product);

	@Mapping(target = "sizeResponses", source = "sizes")
	ColorResponse mapColorToColorResponse(Color color);

	SizeResponse mapSizeToSizeResponse(Size size);

	ImageResponse mapImageToImageResponse(Image image);

	List<ProductResponse> mapModelsToResponses(List<Product> products);


	//map product to productItemResponse
	@Mapping(target = "imageResponses", source = "images")
	ProductItemResponse mapModelToProductItemResponse(Product product);

	@Mapping(target = "category.id", source = "categoryId")
	@Mapping(target = "user.id", source = "userId")
	Product mapRequestedToModel(ProductRequest productRequest);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "category.id", source = "categoryId")
	@Mapping(target = "user.id", source = "userId")
	void updateModel(@MappingTarget Product product, ProductRequest productRequest);
}
