package com.app.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.organization.entity.OrganizationUser;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, Long> {

}