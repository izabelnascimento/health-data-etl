package com.izabel.health.data.etl.api.service;


import com.izabel.health.data.etl.dto.AggregationCityDTO;
import com.izabel.health.data.etl.dto.AggregationDTO;
import com.izabel.health.data.etl.loader.CityRepository;
import com.izabel.health.data.etl.loader.CoverageRepository;
import com.izabel.health.data.etl.loader.HealthCareVisitRepository;
import com.izabel.health.data.etl.mapper.CityMapper;
import com.izabel.health.data.etl.model.City;
import com.izabel.health.data.etl.model.Coverage;
import com.izabel.health.data.etl.model.HealthCareVisit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregationService {

    private final CityRepository cityRepository;
    private final CoverageRepository coverageRepository;
    private final HealthCareVisitRepository healthCareVisitRepository;

    public List<AggregationCityDTO> aggregate(Long year) {
        return cityRepository.findAll().stream()
                .map(city -> {
                    List<AggregationDTO> aggregationList = aggregateForCityAndYear(city, year);
                    return new AggregationCityDTO(CityMapper.toCityResponseDTO(city), aggregationList);
                })
                .toList();
    }

    public AggregationCityDTO aggregate(Long cityId, Long year) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new IllegalArgumentException("City not found: " + cityId));
        List<AggregationDTO> aggregationList = aggregateForCityAndYear(city, year);
        return new AggregationCityDTO(CityMapper.toCityResponseDTO(city), aggregationList);
    }

    private List<AggregationDTO> aggregateForCityAndYear(City city, Long year) {
        List<AggregationDTO> aggregationDTOs = new ArrayList<>();
        for (long month = 1; month <= 12; month++) {
            try {
                aggregateForMonth(city, year, month).ifPresent(aggregationDTOs::add);
            } catch (Exception e) {
                log.warn("Skipped cityId={}, year={}, month={} due to: {}", city.getId(), year, month, e.getMessage());
            }
        }
        return aggregationDTOs;
    }

    public Optional<AggregationDTO> aggregateForMonth(City city, Long year, Long month) {
        Long cityId = city.getId();
        Optional<Coverage> coverage = coverageRepository.findByYearAndMonthAndCity_Id(year, month, cityId);
        Optional<HealthCareVisit> healthCareVisit = healthCareVisitRepository.findByYearAndMonthAndCity_Id(year, month, cityId);

        if (coverage.isEmpty() || healthCareVisit.isEmpty()) {
            return Optional.empty();
        }

        Double teamsDensity = coverage.get().getTeamsDensity();
        Long coverageNumber = coverage.get().getCoverageNumber();
        Double coveragePercentage = coverage.get().getCoveragePercent();
        Long productivity = healthCareVisit.get().getVisits();

        return Optional.of(new AggregationDTO(
                month,
                teamsDensity,
                coverageNumber,
                coveragePercentage,
                productivity,
                coverageNumber == 0 ? 0.0 : (double) productivity / coverageNumber
        ));
    }
}
