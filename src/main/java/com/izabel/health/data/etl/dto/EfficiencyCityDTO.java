package com.izabel.health.data.etl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EfficiencyCityDTO {
    @NonNull
    private CityResponseDTO city;
    @NonNull
    private List<EfficiencyDTO> efficiencies;
    private Double avgEfficiency = 0.0;
}
