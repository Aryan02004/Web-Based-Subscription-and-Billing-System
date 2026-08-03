package com.app.organization.entity;

import java.time.LocalDateTime;

import com.app.auth.entity.Role;
import com.app.auth.entity.User;
import com.app.common.entity.BaseEntity;
import com.app.common.enums.OrganizationUserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organization_users")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationUser extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@Column(name = "joined_at")
	private LocalDateTime joinedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrganizationUserStatus status;
}
