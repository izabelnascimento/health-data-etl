package com.izabel.health.data.etl.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CityResponseDTO {
    private Long id;
    private String name;
}
