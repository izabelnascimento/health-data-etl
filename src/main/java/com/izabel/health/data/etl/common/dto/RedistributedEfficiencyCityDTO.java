package com.izabel.health.data.etl.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RedistributedEfficiencyCityDTO {
    private RankedEfficiencyCityDTO real;
    private RankedEfficiencyCityDTO redistributed;
}
