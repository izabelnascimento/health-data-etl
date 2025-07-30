package com.izabel.health.data.etl.etl.controller;

import com.izabel.health.data.etl.api.service.CoverageETLService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/etl/coverage")
@AllArgsConstructor
public class CoverageETLController {

//    TODO POR ENQUANTO É UM CONTROLLER, MAS A IDEIA É QUE SEJA UM SCHEDULER MENSAL

    private final CoverageETLService coverageETLService;

    @PostMapping("/collect")
    public int collectPECitiesCoverageData() {
        return coverageETLService.collectPECitiesCoverageData();
    }
}

