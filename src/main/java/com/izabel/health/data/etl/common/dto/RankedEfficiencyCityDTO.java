package com.izabel.health.data.etl.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RankedEfficiencyCityDTO {
    private List<EfficiencyCityDTO> top;
    private List<EfficiencyCityDTO> down;
}
