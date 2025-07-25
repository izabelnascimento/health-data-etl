package com.izabel.health.data.etl.api.service;

import com.izabel.health.data.etl.dto.CityYearAggregationDTO;
import com.izabel.health.data.etl.dto.CityYearValueDTO;
import com.izabel.health.data.etl.dto.HealthCareVisitAggregationDTO;
import com.izabel.health.data.etl.dto.HealthCareVisitResponseDTO;
import com.izabel.health.data.etl.loader.CityRepository;
import com.izabel.health.data.etl.loader.HealthCareVisitRepository;
import com.izabel.health.data.etl.mapper.CityMapper;
import com.izabel.health.data.etl.model.City;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthCareVisitService {

    private final HealthCareVisitRepository healthCareVisitRepository;
    private final CityRepository cityRepository;

    public List<HealthCareVisitResponseDTO> getHealthCareVisitByYear(Long year) {
        List<City> cities = cityRepository.findAll();
        Map<Long, City> cityMap = cities.stream()
                .collect(Collectors.toMap(City::getId, Function.identity()));

        List<HealthCareVisitAggregationDTO> totals = healthCareVisitRepository.findTotalVisitsByCityAndYear(year);

        return totals.stream()
                .map(result -> HealthCareVisitResponseDTO.builder()
                        .city(CityMapper.toCityResponseDTO(cityMap.get(result.getCityId())))
                        .value(result.getTotal())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CityYearValueDTO> getAnnualSumByCity(Long cityId) {
        List<CityYearAggregationDTO> results = healthCareVisitRepository.findAnnualSumByCity(cityId);

        return results.stream()
                .map(r -> new CityYearValueDTO(r.getYear(), r.getTotal()))
                .collect(Collectors.toList());
    }

}

