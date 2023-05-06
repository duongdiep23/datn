package com.dshop.dshop.services.impl;

import com.dshop.dshop.exception.ResourceNotFoundException;
import com.dshop.dshop.libraries.Utilities;
import com.dshop.dshop.mapper.CategoryMapper;
import com.dshop.dshop.models.Category;
import com.dshop.dshop.models.dtos.CategoryDTO;
import com.dshop.dshop.repositories.CategoryRepository;
import com.dshop.dshop.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	private final CategoryMapper categoryMapper;

	private final Utilities utilities;
	@Autowired
	public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper,
			Utilities utilities) {
		super();
		this.categoryRepository = categoryRepository;
		this.categoryMapper = categoryMapper;
		this.utilities = utilities;
	}

	@Override
	public List<CategoryDTO> getAllCategories() {
		// TODO Auto-generated method stub
		List<Category> categories = categoryRepository.findAll();
		return categoryMapper.mapModelToDTOs(categories);
	}

	@Override
	public CategoryDTO getCategoryById(long categoryId) {
		// TODO Auto-generated method stub
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));

		return categoryMapper.mapModelToDTO(category);
	}

	@Override
	public CategoryDTO createCategory(CategoryDTO categoryDTO) {
		// TODO Auto-generated method stub

		// Get current date and set categoryCreatedDate, categoryLastModified
		categoryDTO.setCreatedDate(utilities.getCurrentDate());
		categoryDTO.setModifiedDate(utilities.getCurrentDate());

		// Set default status
		categoryDTO.setStatus(1);

		// Convert and save
		Category category = categoryRepository.save(categoryMapper.mapDTOToModel(categoryDTO));

		return categoryMapper.mapModelToDTO(category);
	}

	@Override
	public CategoryDTO updateCategory(CategoryDTO categoryDTO, long categoryId) {
		// TODO Auto-generated method stub
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));

		categoryDTO.setId(categoryId);
		categoryDTO.setModifiedDate(utilities.getCurrentDate());

		category = categoryMapper.mapDTOToModel(categoryDTO);

		// Save
		Category responseCategory = categoryRepository.save(category);

		return categoryMapper.mapModelToDTO(responseCategory);
	}

	@Override
	public void deleteCategory(long categoryId) {
		// TODO Auto-generated method stub
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));
		try {
			categoryRepository.delete(category);
		} catch (Exception ex) {
			System.out.print("Ex: " + ex);
		}
	}

}
