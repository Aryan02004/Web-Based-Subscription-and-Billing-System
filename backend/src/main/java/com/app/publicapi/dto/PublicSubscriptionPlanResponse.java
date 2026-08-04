package com.app.publicapi.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicSubscriptionPlanResponse {

    private Long id;
    private String planName;
    private String description;
    private BigDecimal price;
    private Integer maxUsers;
    private Integer storageLimitGb;
    private Map<String, Object> features;
    private Boolean active;
    private String billingCycle;
}
