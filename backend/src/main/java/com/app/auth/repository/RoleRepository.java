package com.app.auth.repository;

import com.app.auth.entity.Role;
import com.app.common.enums.RoleType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(RoleType name);

	boolean existsByName(RoleType name);

}