package com.izabel.health.data.etl.service;

import com.izabel.health.data.etl.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository repository;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://siops-consulta-publica-api.saude.gov.br/v1")
            .build();

    public String fetchAndPrintRawJson() {
        String jsonResponse = webClient.get()
                .uri("/despesas-por-subfuncao/26/261160/2025/14")
                .header("accept", "application/json")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("Resposta da API SIOPS:");
        System.out.println(jsonResponse);
        return jsonResponse;
    }
}

