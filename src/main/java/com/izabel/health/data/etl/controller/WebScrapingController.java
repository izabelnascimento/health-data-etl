package com.izabel.health.data.etl.controller;

import com.izabel.health.data.etl.service.BudgetService;
import com.izabel.health.data.etl.service.WebScrapingService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/coletar")
@AllArgsConstructor
public class WebScrapingController {

    private final WebScrapingService webScrapingService;
    private final BudgetService budgetService;

    @GetMapping
    public String iniciar() {
        try {
            webScrapingService.extrairEDepositar();
            return "Valores extraídos e salvos com sucesso.";
        } catch (IOException e) {
            return "Erro: " + e.getMessage();
        }
    }

    @PostMapping("/siops")
    public String collectSiopsData() {
        return budgetService.fetchAndPrintRawJson();
//        return "SIOPS data successfully collected.";
    }
}

