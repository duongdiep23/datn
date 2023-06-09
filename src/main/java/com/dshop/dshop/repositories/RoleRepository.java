package com.dshop.dshop.repositories;

import com.dshop.dshop.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
	@Override
	Optional<Role> findById(Long aLong);

	Optional<Role> findByName(String name);
}
