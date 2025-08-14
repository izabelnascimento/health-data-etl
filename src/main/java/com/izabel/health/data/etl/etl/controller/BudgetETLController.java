package com.izabel.health.data.etl.etl.controller;

import com.izabel.health.data.etl.common.model.Budget;
import com.izabel.health.data.etl.etl.service.BudgetETLService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/etl/budget")
@AllArgsConstructor
public class BudgetETLController {

//    TODO POR ENQUANTO É UM CONTROLLER, MAS A IDEIA É QUE SEJA UM SCHEDULER MENSAL

    private final BudgetETLService budgetETLService;

    @PostMapping
    public Budget collectBudgetData() {
        return budgetETLService.fetchAndSaveBudget();
    }

    @PostMapping("/collect")
    public int collectPECitiesBudgetData() {
        return budgetETLService.fetchAndSaveCitiesBudget();
    }
}

