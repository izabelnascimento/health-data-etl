package com.izabel.health.data.etl.etl.source;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class Siops {
    public final int PE_ID = 26;
    public final WebClient webClient = WebClient.builder()
            .baseUrl("https://siops-consulta-publica-api.saude.gov.br/v1")
            .build();
    public final List<Long> YEARS = List.of(2020L, 2021L, 2022L, 2023L, 2024L);
    public static final List<Long> BIMESTERS = List.of(12L, 14L, 1L, 18L, 20L, 2L);
}
