package com.izabel.health.data.etl.service;

import com.izabel.health.data.etl.dto.BudgetDTO;
import com.izabel.health.data.etl.extractor.BudgetExtractor;
import com.izabel.health.data.etl.model.Budget;
import com.izabel.health.data.etl.loader.BudgetRepository;
import com.izabel.health.data.etl.transformer.BudgetTransformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetETLService {

    private final BudgetRepository loading;
    private final BudgetExtractor extractor;
    private final BudgetTransformation transformation;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://siops-consulta-publica-api.saude.gov.br/v1")
            .build();

    public List<BudgetDTO> fetchAndPrintRawJson() {
        var jsonResponse = webClient.get()
                .uri("/despesas-por-subfuncao/26/261160/2025/14")
                .header("accept", "application/json")
                .retrieve()
                .bodyToFlux(BudgetDTO.class)
                .collectList()
                .block();

        System.out.println("Resposta da API SIOPS:");
        System.out.println(jsonResponse);
        return jsonResponse;
    }

    public Budget fetchAndSaveBudget() {
        var response = extractor.extract();
        Budget budget = transformation.transform(response);

        return loading.save(budget);
    }
}

