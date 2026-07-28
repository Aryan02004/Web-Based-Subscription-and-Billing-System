package com.app.organization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.organization.entity.OrganizationUser;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, Long> {
	Optional<OrganizationUser> findByOrganizationIdAndUserId(Long organizationId, Long userId);

	List<OrganizationUser> findByUserId(Long userId);
}