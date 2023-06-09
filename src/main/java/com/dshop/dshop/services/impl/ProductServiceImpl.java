package com.dshop.dshop.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dshop.dshop.exception.ResourceNotFoundException;
import com.dshop.dshop.libraries.Utilities;
import com.dshop.dshop.mapper.ColorMapper;
import com.dshop.dshop.mapper.ImageMapper;
import com.dshop.dshop.mapper.ProductMapper;
import com.dshop.dshop.mapper.SizeMapper;
import com.dshop.dshop.models.Color;
import com.dshop.dshop.models.Image;
import com.dshop.dshop.models.Product;
import com.dshop.dshop.models.Size;
import com.dshop.dshop.models.request.ColorRequest;
import com.dshop.dshop.models.request.ProductRequest;
import com.dshop.dshop.models.request.SizeRequest;
import com.dshop.dshop.models.response.ProductResponse;
import com.dshop.dshop.repositories.ColorRepository;
import com.dshop.dshop.repositories.ImageRepository;
import com.dshop.dshop.repositories.ProductRepository;
import com.dshop.dshop.repositories.SizeRepository;
import com.dshop.dshop.services.ProductService;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;


	private final ImageRepository imageRepository;


	private final ColorRepository colorRepository;


	private final SizeRepository sizeRepository;


	private final ProductMapper productMapper;

	private final ImageMapper imageMapper;

	private final ColorMapper colorMapper;

	private final SizeMapper sizeMapper;


	private final Utilities utilities;

	@Autowired
	public ProductServiceImpl(ProductRepository productRepository, ImageRepository imageRepository,
							  ColorRepository colorRepository, SizeRepository sizeRepository, ProductMapper productMapper,
							  ImageMapper imageMapper, ColorMapper colorMapper, SizeMapper sizeMapper, Utilities utilities) {
		this.productRepository = productRepository;
		this.imageRepository = imageRepository;
		this.colorRepository = colorRepository;
		this.sizeRepository = sizeRepository;
		this.productMapper = productMapper;
		this.imageMapper = imageMapper;
		this.colorMapper = colorMapper;
		this.sizeMapper = sizeMapper;
		this.utilities = utilities;
	}

	private void saveColorsAndSizes(List<ColorRequest> colorRequests, long productId) {
		if (colorRequests != null) {
			for (ColorRequest colorRequest : colorRequests) {
				colorRequest.setProductId(productId);
				Color color = colorMapper.mapRequestedToModel(colorRequest);
				color.setSizes(null);
				Color newColor = colorRepository.save(color);
				long newColorId = newColor.getId();
				if (newColorId == 0) {
					break;
				} else {
					List<SizeRequest> sizeRequests = colorRequest.getSizes().stream().toList();
					for (SizeRequest sizeRequest : sizeRequests) {
						sizeRequest.setColorId(newColorId);
						Size size = sizeMapper.mapRequestedToModel(sizeRequest);
						size.setSold(0);
						sizeRepository.save(size);
					}
				}

			}
		}

	}

	private void saveImages(MultipartFile[] images, long productId) throws IOException {
		// Lặp qua từng file trong danh sách images.
		for (MultipartFile file : images) {
			// Lấy tên file và extension.
			String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
			String extension = FilenameUtils.getExtension(filename);

			// Tạo đường dẫn tới file ảnh.
			String imagePath = "/assets/images/uploads/" + filename;

			// Tạo file mới với đường dẫn được chỉ định.
			
			File savedFile = new File("D:/dshop/src/main/resources/static/assets/images/uploads/"+ filename);

			// Lưu file vào đường dẫn.
			try (OutputStream outputStream = new FileOutputStream(savedFile)) {
				outputStream.write(file.getBytes());
			}
			// Tạo một đối tượng Image mới và thiết lập thuộc tính url.
			Image image = new Image();
			image.setUrl(imagePath);
			image.setProduct(productRepository.findById(productId).orElseThrow());

			// Lưu đối tượng Image vào cơ sở dữ liệu.
			imageRepository.save(image);
		}
	}

	@Transactional
	@Override
	public ProductResponse createProduct(ProductRequest productRequest, MultipartFile[] images) throws IOException {
		String str = productRequest.getName();
		String[] parts = str.split(" ");
		StringBuilder sb = new StringBuilder();
		int count = 0; // biến đếm
		for (String part : parts) {
			if (count >= 3) { // kiểm tra giới hạn 3 kí tự
				break;
			}
			sb.append(part.charAt(0)); // lấy kí tự đầu tiên của phần tử
			count++; // tăng biến đếm
		}

		String sku = sb.toString() +"U"+ String.valueOf(productRequest.getUserId()) +"P"+ String.valueOf(productRepository.findNewestId());
		sku = sku.toUpperCase(); // chuyển thành chữ hoa

		productRequest.setSku(sku);
		Product product = productMapper.mapRequestedToModel(productRequest);

		// set current date
		product.setCreatedDate(utilities.getCurrentDate());
		product.setModifiedDate(utilities.getCurrentDate());

		product.setVisited(-1);
		product.setStatus(1);

		product.setImages(null);
		// Save new product to database
		Product newProduct = productRepository.save(product);

		// Get id of product create recently
		long productId = newProduct.getId();

		List<ColorRequest> colorRequests = productRequest.getColors();
		this.saveColorsAndSizes(colorRequests, productId);

		// Save image into table image
		this.saveImages(images, productId);

		return this.getProductById(productId);
	}

	// Save color and size into database

	/**
	 * Lấy danh sách tất cả sản phẩm, phân trang theo
	 */
	@Transactional
	@Override
	public Page<ProductResponse> getAllProducts(int pageNumber, int pageSize, String sortBy) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
		Page<Product> products = productRepository.findAll(pageable);
		return products.map(productMapper::toProductResponse);
	}

	@Override
	public long countProduct() {
		return productRepository.count();
	}

	/**
	 * Lấy danh sách sản phẩm theo mã danh mục
	 */
	@Transactional
	@Override
	public Page<ProductResponse> getProductsByCategoryId(long categoryId, int pageNumber, int pageSize, String sortBy) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
		Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
		return products.map(productMapper::toProductResponse);
	}

	@Override
	public Optional<List<ProductResponse>> searchProducts(String search) {
		List<Product> products = productRepository.searchProducts(search);

		if (!products.isEmpty()) {
			List<ProductResponse> productResponses = productMapper.mapModelsToResponses(products);
			return Optional.of(productResponses);
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Lấy thông tin một sản phẩm theo product_id
	 */
	@Transactional
	@Override
	public ProductResponse getProductById(long productId) {
		productRepository.incrementVisitedById(productId);
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
		return productMapper.toProductResponse(product);
	}

	@Override
	public ProductResponse updateProduct(ProductRequest productRequest, long productId) {
		// get product by id from the database
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

		productMapper.updateModel(product, productRequest);
		product.setModifiedDate(utilities.getCurrentDate());

		Product responseProduct = productRepository.save(product);

		return productMapper.toProductResponse(responseProduct);
	}

	@Override
	public void deleteProductById(long productId) {
		// get product by id from the database
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
		try {
			productRepository.delete(product);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print("Ex: " + e);
		}

	}

	@Override
	public void actionProduct(Long productId, int action) {
		Product product = productRepository.findById(productId).orElseThrow();
		try{
			if(action == 0){
				product.setStatus(1);
			}else if (action == 1){
				product.setStatus(0);
			}
			productRepository.save(product);
		}catch (Exception ex){
			System.out.print("Ex: " + ex);
		}
	}

}
