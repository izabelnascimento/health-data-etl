package com.izabel.health.data.etl.extractor;

import com.izabel.health.data.etl.source.Siops;
import com.izabel.health.data.etl.source.Sisab;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.izabel.health.data.etl.source.Sisab.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class HealthCareVisitExtractor extends Siops {

    public void batchExtract() throws IOException {
        log.info("Iniciando extração de dados");
        for (Long year: Sisab.YEARS) {
            log.info("Extração do ano: {}", year);
            for (Long production : Sisab.PRODUCTION) {
                extract(getDateCodes(year), production, year);
            }
        }
        log.info("Extração de dados finalizada com sucesso");
    }

    public void extract(List<String> dateCodes, Long production, Long year) throws IOException {
        Connection.Response initialResponse = Jsoup.connect(BASE_URL)
                .method(Connection.Method.GET)
                .timeout(TIMEOUT)
                .execute();

        Map<String, String> cookies = initialResponse.cookies();
        Document initialDoc = initialResponse.parse();
        String initialViewState = initialDoc.select("input[name=javax.faces.ViewState]").val();

        Connection.Response onchangeResponse = Jsoup.connect(BASE_URL)
                .timeout(TIMEOUT)
                .cookies(cookies)
                .method(Connection.Method.POST)
                .header("Faces-Request", "partial/ajax")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .data("javax.faces.partial.ajax", "true")
                .data("javax.faces.source", "unidGeo")
                .data("javax.faces.partial.execute", "unidGeo")
                .data("javax.faces.partial.render", "regioes")
                .data("unidGeo", "estado")
                .data("javax.faces.behavior.event", "valueChange")
                .data("javax.faces.partial.event", "change")
                .data("javax.faces.ViewState", initialViewState)
                .execute();

        String updatedViewState = Jsoup.parse(onchangeResponse.body())
                .select("update[id=javax.faces.ViewState]").text();

        Connection request = Jsoup.connect(BASE_URL)
                .timeout(TIMEOUT)
                .cookies(cookies)
                .method(Connection.Method.POST)
                .header("Content-Type", "application/x-www-form-urlencoded");

        request.data("j_idt44", "j_idt44");
        request.data("lsCid", "");
        request.data("dtBasicExample_length", "10");
        request.data("lsSigtap", "");
        request.data("td-ls-sigtap_length", "10");
        request.data("unidGeo", "estado");

        for (String estado : STATES) {
            request.data("estados", estado);
        }

        for (String comp : dateCodes) {
            request.data("j_idt76", comp);
        }

        request.data("selectLinha", "MUN.CO_MUNICIPIO_IBGE");
        request.data("selectcoluna", "NU_COMPETENCIA");
        request.data("idadeInicio", "0");
        request.data("idadeFim", "0");
        request.data("tpProducao", production.toString());
        request.data("javax.faces.ViewState", updatedViewState);
        request.data("j_idt192", "j_idt192");

        Connection.Response postResponse = request.execute();
        String htmlResponse = postResponse.body();

        saveAsCsv(htmlResponse, year, production);
    }

    private void saveAsCsv(String html, Long year, Long production) {
        try {
            File dir = new File("src/main/resources/sisab");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File file = new File(dir, year + "_production_" + production + "_" + timestamp + ".csv");

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(html);
                log.info("Arquivo salvo em: {}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Erro ao salvar o arquivo CSV", e);
        }
    }
}
