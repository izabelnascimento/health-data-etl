package com.izabel.health.data.etl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthCareVisitDTO {
    private String competencia;
    private int atendimentoIndividual;
    private int atendimentoOdontologico;
    private int procedimento;
    private int visitaDomiciliar;
}

