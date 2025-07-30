package com.izabel.health.data.etl.api.controller;

import com.izabel.health.data.etl.api.service.HealthCareVisitService;
import com.izabel.health.data.etl.common.dto.CityYearValueDTO;
import com.izabel.health.data.etl.common.dto.HealthCareVisitResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-care-visit")
@AllArgsConstructor
public class HealthCareVisitController {

    private final HealthCareVisitService healthCareVisitService;

    @PostMapping("/city")
    public List<HealthCareVisitResponseDTO> collectHealthCareVisitData(@RequestParam Long year) {
        return healthCareVisitService.getHealthCareVisitByYear(year);
    }

    @GetMapping("/summary-by-city")
    public List<CityYearValueDTO> getSummaryByCity(@RequestParam Long cityId) {
        return healthCareVisitService.getAnnualSumByCity(cityId);
    }

}
