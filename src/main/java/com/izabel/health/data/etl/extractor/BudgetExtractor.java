package com.izabel.health.data.etl.extractor;

import com.izabel.health.data.etl.dto.BudgetDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BudgetExtractor {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://siops-consulta-publica-api.saude.gov.br/v1")
            .build();

    public List<BudgetDTO> extract() {
        return webClient.get()
                .uri("/despesas-por-subfuncao/26/261160/2025/14")
                .header("accept", "application/json")
                .retrieve()
                .bodyToFlux(BudgetDTO.class)
                .collectList()
                .block();
    }
}
