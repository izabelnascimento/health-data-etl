package com.izabel.health.data.etl.extractor;

import com.izabel.health.data.etl.dto.CityDTO;
import com.izabel.health.data.etl.source.Siops;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CityExtractor extends Siops {

    public List<CityDTO> extract() {
        return webClient.get()
                .uri("/ente/municipal/" + PE_ID)
                .header("accept", "application/json")
                .retrieve()
                .bodyToFlux(CityDTO.class)
                .collectList()
                .block();
    }
}
