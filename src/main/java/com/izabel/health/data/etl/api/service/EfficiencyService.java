package com.izabel.health.data.etl.api.service;


import com.izabel.health.data.etl.common.dto.EfficiencyCityDTO;
import com.izabel.health.data.etl.common.dto.EfficiencyDTO;
import com.izabel.health.data.etl.common.dto.RankedEfficiencyCityDTO;
import com.izabel.health.data.etl.common.dto.RedistributedEfficiencyCityDTO;
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
                calculateEfficiency(coverageNumber, productivity)
        ));
    }

    public RankedEfficiencyCityDTO getTopAndBottomEfficientCities(Long year) {
        List<EfficiencyCityDTO> allCities = efficiency(year);

        List<EfficiencyCityDTO> sorted = allCities.stream()
                .peek(dto -> dto.setAvgEfficiency(averageEfficiency(dto.getEfficiencies())))
                .sorted(Comparator.comparingDouble(EfficiencyCityDTO::getAvgEfficiency))
                .toList();

        return new RankedEfficiencyCityDTO(
                sorted.subList(Math.max(0, sorted.size() - 5), sorted.size()),
                sorted.subList(0, Math.min(5, sorted.size()))
        );
    }

    public RedistributedEfficiencyCityDTO redistributeResources(Long year) {
        RankedEfficiencyCityDTO real = getTopAndBottomEfficientCities(year);
        RankedEfficiencyCityDTO redistributed = redistributeResources(real);
        return new RedistributedEfficiencyCityDTO(real, redistributed);
    }

    private RankedEfficiencyCityDTO redistributeResources(RankedEfficiencyCityDTO real) {
        final double CAP = 1.0;

        Map<Integer, List<EfficiencyDTO>> topByMonth = groupByMonth(real.getTop());
        Map<Integer, List<EfficiencyDTO>> downByMonth = groupByMonth(real.getDown());

        Map<Integer, Double> monthSurplus = new HashMap<>();
        Map<Integer, Double> monthDelta = new HashMap<>();

        for (int m = 1; m <= 12; m++) {
            List<EfficiencyDTO> topList = topByMonth.getOrDefault(m, List.of());
            List<EfficiencyDTO> downList = downByMonth.getOrDefault(m, List.of());

            double surplus = topList.stream()
                    .mapToDouble(e -> Math.max(nullSafe(e.getEfficiency()) - CAP, 0.0))
                    .sum();

            double delta = (downList.isEmpty() ? 0.0 : surplus / downList.size());

            monthSurplus.put(m, surplus);
            monthDelta.put(m,   delta);
        }

        List<EfficiencyCityDTO> topAdjusted = real.getTop().stream()
                .map(city -> new EfficiencyCityDTO(
                        city.getCity(),
                        city.getEfficiencies().stream()
                                .map(e -> copyWithNewEfficiency(e, Math.min(nullSafe(e.getEfficiency()), CAP)))
                                .toList(),
                        averageEfficiency(
                                city.getEfficiencies().stream()
                                        .map(e -> copyWithNewEfficiency(e, Math.min(nullSafe(e.getEfficiency()), CAP)))
                                        .toList())
                ))
                .toList();

        List<EfficiencyCityDTO> downAdjusted = real.getDown().stream()
                .map(city -> new EfficiencyCityDTO(
                        city.getCity(),
                        city.getEfficiencies().stream()
                                .map(e -> {
                                    double base = nullSafe(e.getEfficiency());
                                    double delta = monthDelta.getOrDefault(e.getMonth().intValue(), 0.0);
                                    return copyWithNewEfficiency(e, base + delta);
                                })
                                .toList(),
                        averageEfficiency(
                                city.getEfficiencies().stream()
                                        .map(e -> {
                                            double base = nullSafe(e.getEfficiency());
                                            double delta = monthDelta.getOrDefault(e.getMonth().intValue(), 0.0);
                                            return copyWithNewEfficiency(e, base + delta);
                                        })
                                        .toList())
                ))
                .toList();

        return new RankedEfficiencyCityDTO(topAdjusted, downAdjusted);
    }

    private Map<Integer, List<EfficiencyDTO>> groupByMonth(List<EfficiencyCityDTO> cities) {
        Map<Integer, List<EfficiencyDTO>> map = new HashMap<>();
        for (EfficiencyCityDTO c : cities) {
            for (EfficiencyDTO e : c.getEfficiencies()) {
                if (e != null && e.getMonth() != null) {
                    map.computeIfAbsent(e.getMonth().intValue(), k -> new ArrayList<>()).add(e);
                }
            }
        }
        return map;
    }

    private EfficiencyDTO copyWithNewEfficiency(EfficiencyDTO src, double newEff) {
        return new EfficiencyDTO(
                src.getMonth(),
                src.getTeamDensity(),
                src.getCoverage(),
                src.getCoveragePercentage(),
                src.getProductivity(),
                newEff
        );
    }

    private double nullSafe(Double v) { return v == null ? 0.0 : v; }

    private Double calculateEfficiency(Long coverageNumber, Long productivity) {
        return coverageNumber == 0 ? 0.0 : (double) productivity / coverageNumber;
    }

    private double averageEfficiency(List<EfficiencyDTO> list) {
        return list.stream().mapToDouble(EfficiencyDTO::getEfficiency).average().orElse(0.0);
    }

}
