package com.izabel.health.data.etl.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetResponseDTO {
    private CityResponseDTO city;
    private Long value;
}
