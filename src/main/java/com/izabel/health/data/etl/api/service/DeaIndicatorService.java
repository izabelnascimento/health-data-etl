package com.izabel.health.data.etl.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izabel.health.data.etl.common.dto.DeaEfficiencyResultDTO;
import com.izabel.health.data.etl.common.dto.DeaIndicatorDTO;
import com.izabel.health.data.etl.common.loader.CityRepository;
import com.izabel.health.data.etl.common.loader.DeaIndicatorRepository;
import com.izabel.health.data.etl.common.mapper.DeaIndicatorMapper;
import com.izabel.health.data.etl.common.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
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
    private final ObjectMapper mapper;

    public static final List<Long> YEARS = List.of(2021L, 2022L, 2023L, 2024L);
    public static final List<Long> FIRST_BIMESTERS = List.of(12L, 14L, 1L);


    public Integer calculate() {
        List<DeaIndicator> deaIndicators = new ArrayList<>();
        List<City> cities = cityRepository.findAll();
        for (Long year : YEARS){
            log.info("Calculating data for DEA {}", year);
            for (Long bimester : BIMESTERS) {
                for (City city : cities) {
                    deaIndicators.add(calculate(year, bimester, city, (long) (BIMESTERS.indexOf(bimester) + 2)));
                }
            }
        }
        log.info("Finished data for DEA calculation");
        return deaIndicatorRepository.saveAll(deaIndicators).size();
    }

    public List<DeaIndicatorDTO> deaGet(Long year, Long bimester) {
        List<DeaIndicator> deaIndicators = deaIndicatorRepository.findByYearAndBimonthly(year, bimester);
        return DeaIndicatorMapper.toDeaDTOs(deaIndicators);
    }

    @Transactional
    public Integer calculateEfficiency() {
        for (Long year : YEARS) {
            log.info("Calculating efficiency with DEA {}", year);
            for (Long bimester : FIRST_BIMESTERS) {
                calculateEfficiency(year, bimester);
            }
        }
        log.info("Finished efficiency calculation");
        return 1;
    }

    private void calculateEfficiency(Long year, Long bimester) {
        List<DeaIndicatorDTO> indicators = deaGet(year, bimester);
        calculateEfficiency(indicators, year, bimester);
    }

    private void calculateEfficiency(List<DeaIndicatorDTO> indicators, Long year, Long bimester) {
        try {
            String scriptPath = "src/main/resources/run_DEA.R";

            ProcessBuilder pb = new ProcessBuilder("Rscript", scriptPath);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            String json = mapper.writeValueAsString(indicators);

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(json);
                writer.flush();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String content = "";
            while ((line = reader.readLine()) != null) {
                content = line;
            }

            int exitCode = process.waitFor();
            System.out.println("Finished with exit code: " + exitCode);

            updateEfficiency(year, bimester, content);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateEfficiency(Long year, Long bimester, String content) throws JsonProcessingException {
        List<DeaEfficiencyResultDTO> deaEfficiencyResultDTOS = mapper.readValue(
                content,
                mapper.getTypeFactory().constructCollectionType(List.class, DeaEfficiencyResultDTO.class)
        );
        deaEfficiencyResultDTOS.forEach(deaEfficiencyResultDTO -> {
            DeaIndicator deaIndicator = deaIndicatorRepository.findFirstByYearAndBimonthlyAndCity_Id(
                    year, bimester, deaEfficiencyResultDTO.cityId()
            );
            deaIndicator.setEfficiency(deaEfficiencyResultDTO.efficiency());
            deaIndicatorRepository.save(deaIndicator);
        });
    }

    private DeaIndicator calculate(Long year, Long bimonthly, City city, Long month) {
        Long cityId = city.getId();
        Budget budget = budgetService.getBudget(cityId, year, bimonthly);
        Coverage coverage = coverageService.getCoverage(cityId, year, month);
        HealthCareVisit healthCareVisit = healthCareVisitService.getHealthCareVisit(cityId, year, month);

        double bimonthlyBudget = budget != null ? budget.getBimonthlyBudget() : 0.0;
        long population = coverage != null ? coverage.getPopulation() : 0;
        Long visits = healthCareVisit != null ? healthCareVisit.getVisits() : 0;
        long teams = coverage != null ? coverage.getTeams() : 0;

        return DeaIndicator.builder()
                .city(city)
                .year(year)
                .bimonthly(bimonthly)
                .apsPerCapita(population > 0 ? bimonthlyBudget / population : 0.0)
                .teamsDensity(coverage != null ? coverage.getTeamsDensity() : 0.0)
                .healthCareVisitsPerThousandReais(bimonthlyBudget > 0 ? visits.doubleValue() / bimonthlyBudget * 1000 : 0.0)
                .coveragePercent(coverage != null ? coverage.getCoveragePercent() : 0.0)
                .productivity(teams > 0 ? visits.doubleValue() / teams : 0.0)
                .population(population)
                .apsTotalSpending(bimonthlyBudget)
                .totalHealthCareVisits(visits)
                .teamsCount(teams)
                .build();
    }

}
