package com.izabel.health.data.etl.common.dto;

import lombok.Builder;

@Builder
public record DeaIndicatorDTO(
        Long cityId,
        String cityName,
        Long bimonthly,
        double apsPerCapita,
        double teamsDensity,
        double healthCareVisitsPerThousandReais,
        double cobertura,
        double productivity,
        double efficiency
) {}
