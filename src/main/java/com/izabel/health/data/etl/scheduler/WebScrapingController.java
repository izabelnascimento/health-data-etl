package com.izabel.health.data.etl.scheduler;

import com.izabel.health.data.etl.dto.BudgetDTO;
import com.izabel.health.data.etl.model.Budget;
import com.izabel.health.data.etl.service.BudgetETLService;
import com.izabel.health.data.etl.service.WebScrapingService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/coletar")
@AllArgsConstructor
public class WebScrapingController {

//    TODO POR ENQUANTO É UM CONTROLLER, MAS A IDEIA É QUE SEJA UM SCHEDULER MENSAL

    private final WebScrapingService webScrapingService;
    private final BudgetETLService budgetETLService;

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
    public List<BudgetDTO> collectSiopsData() {
        return budgetETLService.fetchAndPrintRawJson();
    }

    @PostMapping("/budget")
    public Budget collectBudgetData() {
        return budgetETLService.fetchAndSaveBudget();
    }
}

