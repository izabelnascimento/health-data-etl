package com.izabel.health.data.etl.api.service;

import com.izabel.health.data.etl.common.dto.BudgetResponseDTO;
import com.izabel.health.data.etl.common.dto.CityResponseDTO;
import com.izabel.health.data.etl.common.dto.CityYearValueDTO;
import com.izabel.health.data.etl.common.loader.BudgetRepository;
import com.izabel.health.data.etl.common.loader.CityRepository;
import com.izabel.health.data.etl.common.model.Budget;
import com.izabel.health.data.etl.common.model.City;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.izabel.health.data.etl.etl.source.Siops.BIMESTERS;
import static com.izabel.health.data.etl.etl.source.Sisab.YEARS;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CityRepository cityRepository;

    public List<BudgetResponseDTO> getBudgetByYear(Long year) {
        List<Budget> budgets = budgetRepository.findByYear(year);

        Map<Long, BudgetResponseDTO> grouped = new HashMap<>();

        for (Budget budget : budgets) {
            Long cityId = budget.getCity().getId();
            String cityName = budget.getCity().getName();
            Long value = budget.getCurrentValue() != null ? budget.getCurrentValue().longValue() : 0L;

            grouped.compute(cityId, (id, dto) -> {
                if (dto == null) {
                    return BudgetResponseDTO.builder()
                            .city(CityResponseDTO.builder()
                                    .id(cityId)
                                    .name(cityName)
                                    .build())
                            .value(value)
                            .build();
                } else {
                    dto.setValue(dto.getValue() + value);
                    return dto;
                }
            });
        }

        return new ArrayList<>(grouped.values());
    }

    public List<CityYearValueDTO> getAnnualSumByCity(Long cityId) {
        List<Budget> budgets = budgetRepository.findByCityId(cityId);

        Map<Long, Long> yearToValue = new HashMap<>();

        for (Budget budget : budgets) {
            Long year = budget.getYear();
            Long value = budget.getCurrentValue() != null ? budget.getCurrentValue().longValue() : 0L;

            yearToValue.merge(year, value, Long::sum);
        }

        return yearToValue.entrySet()
                .stream()
                .map(entry -> new CityYearValueDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CityYearValueDTO::getYear))
                .collect(Collectors.toList());
    }

    public Budget getBudget(Long cityId, Long year, Long bimonthly) {
        return budgetRepository.getFirstBudgetByCity_IdAndYearAndBimonthly(cityId, year, bimonthly);
    }

    @Transactional
    public void updateMonthlyValues(){
        List<City> cities = cityRepository.findAll();
        for (Long year: YEARS) {
            log.info("Updating {} budgets", year);
            for (City city : cities) {
                updateMonthlyValues(year, city.getId());
            }
        }
        log.info("Updated budgets for {} years", YEARS.size());
    }

    public void updateMonthlyValues(Long year, Long cityId) {
        List<Budget> budgets = budgetRepository.findByYearAndCity_Id(year, cityId);

        budgets.sort(Comparator.comparingInt(b -> BIMESTERS.indexOf(b.getBimonthly())));

        double previousTotal = 0.0;

        for (Budget budget : budgets) {
            double currentTotal = safe(budget.getCapitalValue()) + safe(budget.getCurrentValue());
            double bimestral = currentTotal - previousTotal;

            if (bimestral < 0){
                currentTotal = previousTotal;
                bimestral = 0;
            }

            long monthly = Math.round(bimestral);

            budget.setBimonthlyBudget((double) monthly);
            budgetRepository.save(budget);

            previousTotal = currentTotal;
        }
    }

    private double safe(Double value) {
        return value == null ? 0.0 : value;
    }
}

