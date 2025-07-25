package com.izabel.health.data.etl.source;

import org.springframework.web.reactive.function.client.WebClient;

public abstract class EGestor {
    public static final int PE_ID = 26;
    public static final int NORTHEAST_ID = 2;
    public static final WebClient WEB_CLIENT = WebClient.builder()
            .baseUrl("https://relatorioaps-prd.saude.gov.br")
            .build();
    public static final Long START = 202001L;
    public static final Long END = 202412L;
}
