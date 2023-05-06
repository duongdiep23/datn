package com.dshop.dshop.repositories;

import com.dshop.dshop.models.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<Size, Long> {
    Size findByValue(String value);
}
