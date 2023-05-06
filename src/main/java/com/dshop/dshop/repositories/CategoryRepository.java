package com.dshop.dshop.repositories;

import com.dshop.dshop.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findAll();
}
