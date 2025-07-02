package com.izabel.health.data.etl.extractor;

import com.izabel.health.data.etl.dto.BudgetDTO;
import com.izabel.health.data.etl.source.Siops;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class BudgetExtractor extends Siops {

    public List<BudgetDTO> extract() {
        return webClient.get()
                .uri("/despesas-por-subfuncao/" + PE_ID + "/261160/2025/14")
                .header("accept", "application/json")
                .retrieve()
                .bodyToFlux(BudgetDTO.class)
                .collectList()
                .block();
    }

    public List<BudgetDTO> extract(Long cityId, Long bimonthly) {
        String uri = String.format("/despesas-por-subfuncao/%s/%s/2025/%s", PE_ID, cityId, bimonthly);
        return webClient.get()
                .uri(uri)
                .header("accept", "application/json")
                .retrieve()
                .bodyToFlux(BudgetDTO.class)
                .collectList()
                .onErrorResume(_ -> {
                    List<BudgetDTO> list = new ArrayList<>();
                    list.add(BudgetDTO.builder().build());
                    list.add(BudgetDTO.builder().build());
                    return Mono.just(list);
                })
                .block();
    }


}