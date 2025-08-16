package com.izabel.health.data.etl.api.controller;

import com.izabel.health.data.etl.api.service.DeaIndicatorService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dea")
@AllArgsConstructor
public class DeaIndicatorController {

    private DeaIndicatorService deaIndicatorService;

    @PostMapping
    public Integer deaIndicator() {
        return deaIndicatorService.calculate();
    }
}
