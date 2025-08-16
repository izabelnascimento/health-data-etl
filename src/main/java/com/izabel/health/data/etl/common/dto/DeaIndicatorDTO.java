package com.izabel.health.data.etl.common.dto;

import lombok.Builder;

@Builder
public record DeaIndicatorDTO(
        Long cityId,
        String municipio,
        double recursoAPSperCapita,
        double densidadeEquipes,
        double atendimentos1000,
        double cobertura,
        double produtividade
) {}
