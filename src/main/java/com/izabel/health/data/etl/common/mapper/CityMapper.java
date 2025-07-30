package com.izabel.health.data.etl.common.mapper;

import com.izabel.health.data.etl.common.dto.CityResponseDTO;
import com.izabel.health.data.etl.common.model.City;

public class CityMapper {
    public static CityResponseDTO toCityResponseDTO(City city) {
        if (city == null) return null;
        return CityResponseDTO.builder()
                .id(city.getId())
                .name(city.getName())
                .build();
    }
}
