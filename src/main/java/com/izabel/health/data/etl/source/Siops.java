package com.izabel.health.data.etl.source;

import org.springframework.web.reactive.function.client.WebClient;

public class Siops {
    public final int PE_ID = 26;
    public final WebClient webClient = WebClient.builder()
            .baseUrl("https://siops-consulta-publica-api.saude.gov.br/v1")
            .build();
}
