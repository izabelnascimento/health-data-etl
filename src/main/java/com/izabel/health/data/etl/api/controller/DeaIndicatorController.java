package com.izabel.health.data.etl.api.controller;

import com.izabel.health.data.etl.api.service.DeaIndicatorService;
import com.izabel.health.data.etl.common.dto.DeaIndicatorDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dea")
@AllArgsConstructor
public class DeaIndicatorController {

    private DeaIndicatorService deaIndicatorService;

    @PostMapping
    public Integer deaIndicator() {
        return deaIndicatorService.calculate();
    }

    @GetMapping("/get")
    public List<DeaIndicatorDTO> deaGet(@RequestParam Long year, @RequestParam Long bimonthly) {
        return deaIndicatorService.deaGet(year, bimonthly);
    }

    @PostMapping("/efficiency")
    public Integer deaEfficiency() {
        return deaIndicatorService.calculateEfficiency();
    }
}
