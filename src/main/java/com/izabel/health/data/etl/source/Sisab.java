package com.izabel.health.data.etl.source;

import java.util.ArrayList;
import java.util.List;

public abstract class Sisab {
    public static final String BASE_URL =
            "https://sisab.saude.gov.br/paginas/acessoRestrito/relatorio/federal/saude/RelSauProducao.xhtml";
    public static final int TIMEOUT = 600_000;
    public static final List<String> STATES = List.of("PE");
    public static final List<Long> YEARS = List.of(2020L, 2021L);
    //    public static final List<Long> YEARS = List.of(2020L, 2021L, 2022L, 2023L, 2024L);
    public static final List<Long> PRODUCTION = List.of(4L, 5L, 7L, 8L);

    public static List<String> getDateCodes(Long year) {
        List<String> codes = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            String code = String.format("%d%02d", year, month);
            codes.add(code);
        }
        return codes;
    }
}
