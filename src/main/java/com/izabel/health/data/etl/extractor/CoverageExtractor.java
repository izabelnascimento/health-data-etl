package com.izabel.health.data.etl.extractor;

import com.izabel.health.data.etl.dto.CoverageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static com.izabel.health.data.etl.source.EGestor.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class CoverageExtractor {

    public List<CoverageDTO> extract(Long cityId) {
        return WEB_CLIENT.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cobertura/aps")
                        .queryParam("unidadeGeografica", "MUNICIPIO")
                        .queryParam("coRegiao", NORTHEAST_ID)
                        .queryParam("coUf", PE_ID)
                        .queryParam("coMunicipio", cityId)
                        .queryParam("nuCompInicio", START)
                        .queryParam("nuCompFim", END)
                        .build()
                )
                .retrieve()
                .bodyToFlux(CoverageDTO.class)
                .collectList()
                .onErrorResume(error -> {
                    log.error("Erro ao desserializar cobertura APS: {}", error.getMessage());
                    return Mono.just(Collections.emptyList());
                })
                .block();
    }


}
