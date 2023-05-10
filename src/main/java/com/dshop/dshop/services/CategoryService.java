package com.dshop.dshop.services;

import com.dshop.dshop.models.dtos.CategoryDTO;
import com.dshop.dshop.models.request.CategoryRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
	Page<CategoryDTO> getCategories(int pageNumber, int pageSize, String sortBy);

	List<CategoryDTO> getAllCategories();

	CategoryDTO getCategoryById(long categoryId);

	CategoryDTO createCategory(CategoryRequest categoryRequest);


	CategoryDTO updateCategory(CategoryRequest categoryRequest, long categoryId);

	/**
	 * Method delete category with categoryId. <br>
	 */
	void deleteCategory(long categoryId);

	void actionCategory(long categoryId,int action);
	long countCategory();
}
