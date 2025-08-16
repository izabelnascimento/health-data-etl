package com.izabel.health.data.etl.common.mapper;


import com.izabel.health.data.etl.common.dto.DeaIndicatorDTO;
import com.izabel.health.data.etl.common.model.DeaIndicator;

import java.util.List;
import java.util.stream.Collectors;

import static com.izabel.health.data.etl.common.util.Util.parseBimester;

public class DeaIndicatorMapper {
    public static DeaIndicatorDTO toDeaDTO(DeaIndicator deaIndicator) {
        return DeaIndicatorDTO.builder()
                .cityId(deaIndicator.getCity().getId())
                .cityName(deaIndicator.getCity().getName())
                .bimonthly(parseBimester(deaIndicator.getBimonthly()))
                .apsPerCapita(deaIndicator.getApsPerCapita())
                .teamsDensity(deaIndicator.getTeamsDensity())
                .healthCareVisitsPerThousandReais(deaIndicator.getHealthCareVisitsPerThousandReais())
                .cobertura(deaIndicator.getCoveragePercent())
                .productivity(deaIndicator.getProductivity())
                .build();
    }

    public static List<DeaIndicatorDTO> toDeaDTOs(List<DeaIndicator> deaIndicators) {
        return deaIndicators.stream().map(DeaIndicatorMapper::toDeaDTO).collect(Collectors.toList());
    }
}
