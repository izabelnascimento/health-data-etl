package com.izabel.health.data.etl.common.dto;

import lombok.Builder;

@Builder
public record DeaEfficiencyResultDTO(
        Long cityId,
        String cityName,
        Long bimonthly,
        Double efficiency
) {}
