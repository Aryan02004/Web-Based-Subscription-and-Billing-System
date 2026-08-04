package com.app.subscription.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.subscription.entity.SubscriptionEntity;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {

	List<SubscriptionEntity> findByOrganizationId(Long organizationId);

	Optional<SubscriptionEntity> findByIdAndOrganizationId(Long id, Long organizationId);

	List<SubscriptionEntity> findByNextBillingDate(java.time.LocalDate nextBillingDate);
}