package com.izabel.health.data.etl.api.controller;

import com.izabel.health.data.etl.api.service.DeaIndicatorService;
import com.izabel.health.data.etl.common.dto.DeaIndicatorDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dea")
@AllArgsConstructor
public class DeaIndicatorController {

    private DeaIndicatorService deaIndicatorService;

    @PostMapping("/calculate/indicators")
    public Integer calculateIndicators() {
        return deaIndicatorService.calculate();
    }

    @GetMapping("/indicators/first-semester")
    public List<DeaIndicatorDTO> getFirstSemesterIndicators(@RequestParam Long year) {
        return deaIndicatorService.getIndicators(year);
    }

    @GetMapping("/indicators/first-semester/ranked")
    public List<DeaIndicatorDTO> getFirstSemesterIndicatorsRanked(@RequestParam Long year) {
        return deaIndicatorService.getTopAndBottomIndicators(year);
    }

    @GetMapping("/indicators")
    public List<DeaIndicatorDTO> getIndicators(@RequestParam Long year, @RequestParam Long bimonthly) {
        return deaIndicatorService.getIndicators(year, bimonthly);
    }

    @PostMapping("/calculate/efficiency")
    public Integer calculateEfficiency() {
        return deaIndicatorService.calculateEfficiency();
    }
}
