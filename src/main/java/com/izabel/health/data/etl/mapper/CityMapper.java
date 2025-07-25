package com.izabel.health.data.etl.mapper;

import com.izabel.health.data.etl.dto.CityResponseDTO;
import com.izabel.health.data.etl.model.City;

public class CityMapper {
    public static CityResponseDTO toCityResponseDTO(City city) {
        if (city == null) return null;
        return CityResponseDTO.builder()
                .id(city.getId())
                .name(city.getName())
                .build();
    }
}
