package com.izabel.health.data.etl.api.service;

import com.izabel.health.data.etl.common.loader.CityRepository;
import com.izabel.health.data.etl.common.loader.DeaIndicatorRepository;
import com.izabel.health.data.etl.common.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.izabel.health.data.etl.etl.source.Siops.BIMESTERS;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeaIndicatorService {
    private final DeaIndicatorRepository deaIndicatorRepository;
    private final BudgetService budgetService;
    private final CoverageService coverageService;
    private final HealthCareVisitService healthCareVisitService;
    private final CityRepository cityRepository;

    public static final List<Long> YEARS = List.of(2021L, 2022L, 2023L, 2024L);

    public Integer calculate() {
        List<DeaIndicator> deaIndicators = new ArrayList<>();
        List<City> cities = cityRepository.findAll();
        for (Long year : YEARS){
            log.info("Calculating DEA {}", year);
            for (Long bimester : BIMESTERS) {
                for (City city : cities) {
                    deaIndicators.add(calculate(year, bimester, city, (long) (BIMESTERS.indexOf(bimester) + 2)));
                }
            }
        }
        log.info("Finished DEA calculation");
        return deaIndicatorRepository.saveAll(deaIndicators).size();
    }

    public DeaIndicator calculate(Long year, Long bimonthly, City city, Long month) {
        Long cityId = city.getId();
        Budget budget = budgetService.getBudget(cityId, year, bimonthly);
        Coverage coverage = coverageService.getCoverage(cityId, year, month);
        HealthCareVisit healthCareVisit = healthCareVisitService.getHealthCareVisit(cityId, year, month);

        Double bimonthlyBudget = budget.getBimonthlyBudget();
        Long population = coverage.getPopulation();
        Long visits = healthCareVisit.getVisits();
        Long teams = coverage.getTeams();

        return DeaIndicator.builder()
                .city(city)
                .year(year)
                .bimonthly(bimonthly)
                .apsPerCapita(bimonthlyBudget / population)
                .teamsDensity(coverage.getTeamsDensity())
                .healthCareVisitsPerThousandReais((visits / bimonthlyBudget * 1000))
                .coveragePercent(coverage.getCoveragePercent())
                .productivity((double) (visits / teams))
                .population(population)
                .apsTotalSpending(bimonthlyBudget)
                .totalHealthCareVisits(visits)
                .teamsCount(teams)
                .build();
    }
}
