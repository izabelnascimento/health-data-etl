package com.izabel.health.data.etl.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CityYearValueDTO {
    private Long year;
    private Long value;
}

