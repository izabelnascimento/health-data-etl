package com.izabel.health.data.etl.common.mapper;


import com.izabel.health.data.etl.common.dto.DeaIndicatorDTO;
import com.izabel.health.data.etl.common.model.DeaIndicator;

import java.util.List;
import java.util.stream.Collectors;

public class DeaIndicatorMapper {
    public static DeaIndicatorDTO toDeaDTO(DeaIndicator deaIndicator) {
        return DeaIndicatorDTO.builder()
                .cityId(deaIndicator.getCity().getId())
                .municipio(deaIndicator.getCity().getName())
                .recursoAPSperCapita(deaIndicator.getApsPerCapita())
                .densidadeEquipes(deaIndicator.getTeamsDensity())
                .atendimentos1000(deaIndicator.getHealthCareVisitsPerThousandReais())
                .cobertura(deaIndicator.getCoveragePercent())
                .produtividade(deaIndicator.getProductivity())
                .build();
    }

    public static List<DeaIndicatorDTO> toDeaDTOs(List<DeaIndicator> deaIndicators) {
        return deaIndicators.stream().map(DeaIndicatorMapper::toDeaDTO).collect(Collectors.toList());
    }
}
