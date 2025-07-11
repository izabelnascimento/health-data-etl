package com.izabel.health.data.etl.api.controller;

import com.izabel.health.data.etl.api.service.BudgetService;
import com.izabel.health.data.etl.dto.BudgetResponseDTO;
import com.izabel.health.data.etl.dto.CityYearValueDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/summary-by-city")
    public List<CityYearValueDTO> getSummaryByCity(@RequestParam Long cityId) {
        return budgetService.getAnnualSumByCity(cityId);
    }


}
