package com.izabel.health.data.etl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CityYearValueDTO {
    private Long year;
    private Long value;
}

