package com.izabel.health.data.etl.service;

import com.izabel.health.data.etl.model.Value;
import com.izabel.health.data.etl.loader.ValorRepository;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class WebScrapingService {

    private final ValorRepository repository;

    public void extrairEDepositar() throws IOException {
        String url = "http://siops.datasus.gov.br/rel_ges_dt_municipal.php";

        Document doc = Jsoup.connect(url)
                .data("cmbAno", "2025")
                .data("cmbUF", "26")
                .data("cmbPeriodo", "14")
                .data("cmbMunicipio[]", "261160")
                .data("BtConsultar", "Consultar")
                .post();

        Elements linhas = doc.select("table#relatorio-dt tbody tr");

        for (Element linha : linhas) {
            Elements colunas = linha.select("td[bgcolor=#FFFFFF].tdr.caixa");

            if (!colunas.isEmpty()) {
                Element tdValor = colunas.last();
                String texto = tdValor.text().replace(".", "").replace(",", ".").trim();

                try {
                    BigDecimal valor = new BigDecimal(texto);
                    Value dado = new Value();
                    dado.setValor(valor);
                    repository.save(dado);
                } catch (NumberFormatException e) {
                    System.out.println("Valor inválido: " + texto);
                }
            }
        }
    }
}
