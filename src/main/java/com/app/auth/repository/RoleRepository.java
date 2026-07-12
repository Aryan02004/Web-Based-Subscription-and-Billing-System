package com.app.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.auth.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}