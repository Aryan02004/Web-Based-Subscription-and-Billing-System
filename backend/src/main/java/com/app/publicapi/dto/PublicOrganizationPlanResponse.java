package com.app.publicapi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicOrganizationPlanResponse {

    private PublicOrganizationResponse organization;
    private List<PublicSubscriptionPlanResponse> plans;
}
