package com.app.publicapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicOrganizationResponse {

    private Long id;
    private String name;
    private String industry;
    private String logo;
}
