package com.izabel.health.data.etl.api.service;


import com.izabel.health.data.etl.common.dto.EfficiencyCityDTO;
import com.izabel.health.data.etl.common.dto.EfficiencyDTO;
import com.izabel.health.data.etl.common.dto.RankedEfficiencyCityDTO;
import com.izabel.health.data.etl.common.loader.CityRepository;
import com.izabel.health.data.etl.common.loader.CoverageRepository;
import com.izabel.health.data.etl.common.loader.HealthCareVisitRepository;
import com.izabel.health.data.etl.common.mapper.CityMapper;
import com.izabel.health.data.etl.common.model.City;
import com.izabel.health.data.etl.common.model.Coverage;
import com.izabel.health.data.etl.common.model.HealthCareVisit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EfficiencyService {

    private final CityRepository cityRepository;
    private final CoverageRepository coverageRepository;
    private final HealthCareVisitRepository healthCareVisitRepository;

    public EfficiencyCityDTO efficiency(Long cityId, Long year) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new IllegalArgumentException("City not found: " + cityId));
        List<EfficiencyDTO> aggregationList = aggregateForCityAndYear(city, year);
        return new EfficiencyCityDTO(CityMapper.toCityResponseDTO(city), aggregationList);
    }

    public List<EfficiencyCityDTO> efficiency(Long year) {
        return cityRepository.findAll().stream()
                .map(city -> {
                    List<EfficiencyDTO> aggregationList = aggregateForCityAndYear(city, year);
                    return new EfficiencyCityDTO(CityMapper.toCityResponseDTO(city), aggregationList);
                })
                .toList();
    }

    private List<EfficiencyDTO> aggregateForCityAndYear(City city, Long year) {
        List<EfficiencyDTO> efficiencyDTOS = new ArrayList<>();
        for (long month = 1; month <= 12; month++) {
            try {
                aggregateForMonth(city, year, month).ifPresent(efficiencyDTOS::add);
            } catch (Exception e) {
                log.warn("Skipped cityId={}, year={}, month={} due to: {}", city.getId(), year, month, e.getMessage());
            }
        }
        return efficiencyDTOS;
    }

    public Optional<EfficiencyDTO> aggregateForMonth(City city, Long year, Long month) {
        Long cityId = city.getId();
        Coverage coverage = coverageRepository.findByYearAndMonthAndCity_Id(year, month, cityId)
                .orElseThrow(() -> new IllegalStateException(
                        "Cobertura não encontrada para " + city.getName() + " em " + month + "/" + year
                ));
        HealthCareVisit healthCareVisit = healthCareVisitRepository.findByYearAndMonthAndCity_Id(year, month, cityId)
                .orElseThrow(() -> new IllegalStateException(
                        "Atendimento não encontrado para " + city.getName() + " em " + month + "/" + year
                ));


        Double teamsDensity = coverage.getTeamsDensity();
        Long coverageNumber = coverage.getCoverageNumber();
        Double coveragePercentage = coverage.getCoveragePercent();
        Long productivity = healthCareVisit.getVisits();

        return Optional.of(new EfficiencyDTO(
                month,
                teamsDensity,
                coverageNumber,
                coveragePercentage,
                productivity,
                coverageNumber == 0 ? 0.0 : (double) productivity / coverageNumber
        ));
    }

    public RankedEfficiencyCityDTO getTopAndBottomEfficientCities(Long year) {
        List<EfficiencyCityDTO> allCities = efficiency(year);

        List<EfficiencyCityDTO> sorted = allCities.stream()
                .peek(dto -> {
                    double avgEfficiency = dto.getEfficiencies().stream()
                            .mapToDouble(EfficiencyDTO::getEfficiency)
                            .average().orElse(0.0);
                    dto.setAvgEfficiency(avgEfficiency);
                })
                .sorted(Comparator.comparingDouble(EfficiencyCityDTO::getAvgEfficiency))
                .toList();

        return new RankedEfficiencyCityDTO(
                sorted.subList(Math.max(0, sorted.size() - 5), sorted.size()),
                sorted.subList(0, Math.min(5, sorted.size()))
        );
    }

}
