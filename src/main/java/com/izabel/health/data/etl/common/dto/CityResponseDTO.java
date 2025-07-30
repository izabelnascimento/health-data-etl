package com.izabel.health.data.etl.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CityResponseDTO {
    private Long id;
    private String name;
}
