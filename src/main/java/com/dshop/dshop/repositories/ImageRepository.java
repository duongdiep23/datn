package com.dshop.dshop.repositories;

import com.dshop.dshop.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
	List<Image> findAll();
}
