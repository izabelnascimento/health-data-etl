package com.izabel.health.data.etl.common.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeaIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    private Long year;
    private Long bimonthly;

    /* ===================== INPUTS (recursos) ===================== */

    private Double apsPerCapita;
    private Double teamsDensity;

    /* ===================== OUTPUTS (resultados) ===================== */
    private Double healthCareVisitsPerThousandReais;
    private Double coveragePercent;
    private Double productivity;

    /* ===================== OPCIONAIS (brutos p/ auditoria/cálculo) ===================== */
    private Long population;
    private Double apsTotalSpending;
    private Long totalHealthCareVisits;
    private Long teamsCount;

    /* ===================== PRODUTO ===================== */
    private Double efficiency;
}

