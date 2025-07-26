package com.izabel.health.data.etl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AggregationCityDTO {
    private CityResponseDTO city;
    private List<AggregationDTO> aggregation;
}
