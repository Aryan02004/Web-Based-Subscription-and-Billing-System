package com.app.organization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.common.enums.OrganizationStatus;
import com.app.organization.entity.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
	List<Organization> findByStatus(OrganizationStatus status);

	Optional<Organization> findByIdAndDeletedFalse(Long id);

	List<Organization> findByStatusAndDeletedFalse(OrganizationStatus status);

	List<Organization> findAllByDeletedFalse();

	List<Organization> findByCreatedByIdAndDeletedFalse(Long userId);

	boolean existsByIdAndDeletedFalse(Long id);
}