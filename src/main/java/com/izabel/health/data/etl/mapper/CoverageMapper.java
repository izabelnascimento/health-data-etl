package com.izabel.health.data.etl.mapper;


import com.izabel.health.data.etl.dto.CoverageDTO;
import com.izabel.health.data.etl.model.City;
import com.izabel.health.data.etl.model.Coverage;

public class CoverageMapper {
    public static Coverage toEntity(CoverageDTO coverageDTO, Long year, Long month, Long teams) {
        return Coverage.builder()
                .city(new City(Long.parseLong(coverageDTO.getCoMunicipioIbge()), "any"))
                .year(year)
                .month(month)
                .population(coverageDTO.getQtPopulacao().longValue())
                .coverageNumber(coverageDTO.getQtCapacidadeEquipe().longValue())
                .teams(teams)
                .coveragePercent(coverageDTO.getQtCobertura())
                .build();
    }
}
