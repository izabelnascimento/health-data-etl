package com.izabel.health.data.etl.etl.transformer;

import com.izabel.health.data.etl.common.dto.CityDTO;
import com.izabel.health.data.etl.common.model.City;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CityTransformation {

    public List<City> transform(List<CityDTO> rawList) {
        return rawList.stream()
                .map(raw -> City.builder()
                        .id(Long.parseLong(raw.getCo_municipio()))
                        .name(raw.getNo_municipio())
                        .build())
                .collect(Collectors.toList());
    }
}
