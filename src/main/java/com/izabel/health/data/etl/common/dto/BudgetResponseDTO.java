package com.izabel.health.data.etl.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetResponseDTO {
    private CityResponseDTO city;
    private Long value;
}
