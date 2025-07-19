package com.izabel.health.data.etl.scheduler;

import com.izabel.health.data.etl.dto.HealthCareVisitDTO;
import com.izabel.health.data.etl.service.BudgetETLService;
import com.izabel.health.data.etl.service.HealthCareVisitETLService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/health-care-visit")
@AllArgsConstructor
public class HealthCareVisitETLController {

//    TODO POR ENQUANTO É UM CONTROLLER, MAS A IDEIA É QUE SEJA UM SCHEDULER MENSAL

    private final HealthCareVisitETLService healthCareVisitETLService;

    @PostMapping("/collect")
    public String collectPECitiesBudgetData() throws IOException, InterruptedException {
        return healthCareVisitETLService.fetchAndSaveCitiesBudget();
    }
}

