package com.app.organization.entity;

import com.app.auth.entity.User;
import com.app.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organizations")
@Setter
@Getter
@NoArgsConstructor
public class Organization extends BaseEntity {

	@Column(nullable = false)
	private String name;

	@Column
	private String industry;

	@Column(name = "contact_email")
	private String contactEmail;

	@Column(nullable = false)
	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	@Column(name = "public_link_token", unique = true)
	private String publicLinkToken;

	@Column(name = "link_active")
	private Boolean linkActive = true;
}
