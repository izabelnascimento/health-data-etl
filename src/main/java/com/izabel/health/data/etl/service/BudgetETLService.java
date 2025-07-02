package com.izabel.health.data.etl.service;

import com.izabel.health.data.etl.dto.BudgetDTO;
import com.izabel.health.data.etl.extractor.BudgetExtractor;
import com.izabel.health.data.etl.loader.CityRepository;
import com.izabel.health.data.etl.model.Budget;
import com.izabel.health.data.etl.loader.BudgetRepository;
import com.izabel.health.data.etl.model.City;
import com.izabel.health.data.etl.transformer.BudgetTransformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetETLService {

    private final BudgetRepository loading;
    private final BudgetExtractor extractor;
    private final BudgetTransformation transformation;
    private final CityRepository cityRepository;

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

    public int fetchAndSaveCitiesBudget() {
        var cities = cityRepository.findAll();
        List<Budget> budgets = new ArrayList<>();
        List<Long> bimesters = List.of(12L, 14L, 1L, 18L, 20L, 2L);

        for(int i=0; i<5; i++) {
            var year = 2020L + i;
            log.info("Iniciando extração de dados do ano: {}", year);
            for (Long bimonthly : bimesters) {
                log.info("Iniciando extração de dados do bimestre: {}", bimonthly);
                for (City city : cities) {
                    Long cityId = city.getId();
                    var response = extractor.extract(cityId, bimonthly);
                    budgets.add(transformation.transform(response, cityId, 26L, year, bimonthly));
                }
            }
        }

        return loading.saveAll(budgets).size();
    }

}

