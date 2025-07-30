package com.izabel.health.data.etl.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EfficiencyDTO {
    private Long month;
    private Double teamDensity;
    private Long coverage;
    private Double coveragePercentage;
    private Long productivity;
    private Double efficiency;
}
