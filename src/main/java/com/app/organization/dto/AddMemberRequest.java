package com.app.organization.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddMemberRequest {

	@NotNull
	private Long userId;
	
	@NotNull
	private Long roleId;
	
}
