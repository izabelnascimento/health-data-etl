package com.izabel.health.data.etl.api.controller;

import com.izabel.health.data.etl.api.service.BudgetService;
import com.izabel.health.data.etl.dto.BudgetResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/budget")
@AllArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping("/city")
    public List<BudgetResponseDTO> collectBudgetData(@RequestParam Long year) {
        return budgetService.getBudgetByYear(year);
    }

}
