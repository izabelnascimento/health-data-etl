package com.izabel.health.data.etl.api.service;

import com.izabel.health.data.etl.dto.BudgetResponseDTO;
import com.izabel.health.data.etl.dto.CityResponseDTO;
import com.izabel.health.data.etl.dto.CityYearValueDTO;
import com.izabel.health.data.etl.loader.BudgetRepository;
import com.izabel.health.data.etl.model.Budget;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;

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

}

