package com.app.auth.entity;

import com.app.common.entity.BaseEntity;
import com.app.common.enums.RoleType;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "name", nullable = false, unique = true, length = 50)
	private RoleType name;

	@Column(name = "description", length = 255)
	private String description;

	public Role() {
	}

	public Role(RoleType name, String description) {
		this.name = name;
		this.description = description;
	}

	public RoleType getName() {
		return name;
	}

	public void setName(RoleType name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}