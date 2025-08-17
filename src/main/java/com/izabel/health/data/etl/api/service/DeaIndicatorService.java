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
import java.util.*;
import java.util.stream.Collectors;

import static com.izabel.health.data.etl.common.util.Util.*;

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

    public Integer calculate() {
        List<DeaIndicator> deaIndicators = new ArrayList<>();
        List<City> cities = cityRepository.findAll();
        for (Long year : COMMON_YEARS){
            log.info("Calculating data for DEA {}", year);
            for (Long bimester : ALL_BIMESTERS_ID) {
                for (City city : cities) {
                    deaIndicators.add(calculate(year, bimester, city, parseBimester(bimester)));
                }
            }
        }
        log.info("Finished data for DEA calculation");
        return deaIndicatorRepository.saveAll(deaIndicators).size();
    }

    public List<DeaIndicatorDTO> getIndicators(Long year) {
        return DeaIndicatorMapper.toDeaDTOs(findIndicators(year));
    }

    public List<DeaIndicatorDTO> getTopAndBottomIndicatorsDTO(Long year, Long rank) {
        return DeaIndicatorMapper.toDeaDTOs(getTopAndBottomIndicators(year, rank));
    }

    public List<DeaIndicatorDTO> getIndicators(Long year, Long bimonthly) {
        List<DeaIndicator> deaIndicators = deaIndicatorRepository.findByYearAndBimonthly(year, bimonthly);
        return DeaIndicatorMapper.toDeaDTOs(deaIndicators);
    }

    @Transactional
    public Integer calculateEfficiency() {
        for (Long year : COMMON_YEARS) {
            log.info("Calculating efficiency with DEA {}", year);
            for (Long bimester : FIRST_BIMESTERS_ID) {
                calculateEfficiency(year, bimester);
            }
        }
        log.info("Finished efficiency calculation");
        return 1;
    }

//    public List<DeaEfficiencyResultDTO> getTopAndBottomIndicatorsRedistributed(Long year, Long rank) {
//        List<DeaIndicator> redistributed = new ArrayList<>();
//
//        List<DeaIndicator> real = getTopAndBottomIndicators(year, rank)
//                .stream()
//                .sorted(Comparator.comparing(DeaIndicator::getEfficiency))
//                .toList();
//
//        int rankInt = rank.intValue()*3;
//        List<DeaIndicator> bottom = real.subList(0, rankInt);
//        List<DeaIndicator> top = real.subList(rankInt, rankInt*2);
//        double percent = 0.10;
//
//        for (DeaIndicator b : top) {
//
//            Coverage coverageTop = coverageService.getCoverage(b.getCity().getId(), year, parseBimester(b.getBimonthly()));
//            Long teams = coverageTop.getTeams();
//            Long teamsToMove = (long) (teams * percent);
//            coverageTop.setTeams(teams - teamsToMove);
//            b.setTeamsDensity(coverageTop.getTeamsDensity());
//
//            redistributed.add(b);
//
//            for (DeaIndicator t : bottom) {
//
//                Coverage coverageBottom = coverageService.getCoverage(t.getCity().getId(), year, parseBimester(t.getBimonthly()));
//                coverageBottom.setTeams(coverageBottom.getTeams() + teamsToMove);
//                t.setTeamsDensity(coverageBottom.getTeamsDensity());
//
//                redistributed.add(t);
//            }
//        }
//
//        String content = calculateEfficiency(DeaIndicatorMapper.toDeaDTOs(redistributed));
//        return jsonToDeaEfficiencyDTO(content);
//    }

    private List<DeaIndicator> getTopAndBottomIndicators(Long year, Long rank) {
        List<DeaIndicator> deaIndicators = findIndicators(year);
        return getTopAndBottomIndicators(deaIndicators, rank);
    }

    private List<DeaIndicator> getTopAndBottomIndicators(List<DeaIndicator> deaIndicators, Long rank) {
        Map<Long, Double> avgEfficiencyByCity = deaIndicators.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getCity().getId(),
                        Collectors.averagingDouble(DeaIndicator::getEfficiency)
                ));

        List<Long> bestCities = avgEfficiencyByCity.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(rank)
                .map(Map.Entry::getKey)
                .toList();

        List<Long> worstCities = avgEfficiencyByCity.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(rank)
                .map(Map.Entry::getKey)
                .toList();

        Set<Long> selectedCities = new HashSet<>();
        selectedCities.addAll(bestCities);
        selectedCities.addAll(worstCities);

        return deaIndicators.stream()
                .filter(d -> selectedCities.contains(d.getCity().getId()))
                .toList();
    }

    private void calculateEfficiency(Long year, Long bimester) {
        List<DeaIndicatorDTO> indicators = getIndicators(year, bimester);
        calculateEfficiency(indicators, year, bimester);
    }

    private void calculateEfficiency(List<DeaIndicatorDTO> indicators, Long year, Long bimester) {
        try {
            String content = calculateEfficiency(indicators);
            updateEfficiency(year, bimester, content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String calculateEfficiency(List<DeaIndicatorDTO> indicators) {
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
            return content;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateEfficiency(Long year, Long bimester, String content) throws JsonProcessingException {
        List<DeaEfficiencyResultDTO> deaEfficiencyResultDTOS = jsonToDeaEfficiencyDTO(content);
        deaEfficiencyResultDTOS.forEach(deaEfficiencyResultDTO -> {
            DeaIndicator deaIndicator = deaIndicatorRepository.findFirstByYearAndBimonthlyAndCity_Id(
                    year, bimester, deaEfficiencyResultDTO.cityId()
            );
            deaIndicator.setEfficiency(deaEfficiencyResultDTO.efficiency());
            deaIndicatorRepository.save(deaIndicator);
        });
    }

    private List<DeaEfficiencyResultDTO> jsonToDeaEfficiencyDTO(String content) {
        try {
            return mapper.readValue(
                    content,
                    mapper.getTypeFactory().constructCollectionType(List.class, DeaEfficiencyResultDTO.class)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private List<DeaIndicator> findIndicators(Long year){
        List<DeaIndicator> deaIndicators = new ArrayList<>();
        for (Long bimonthly : FIRST_BIMESTERS_ID) {
            deaIndicators.addAll(deaIndicatorRepository.findByYearAndBimonthly(year, bimonthly));
        }
        return deaIndicators;
    }

}
