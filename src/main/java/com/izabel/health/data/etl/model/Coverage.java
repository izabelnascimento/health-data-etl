package com.izabel.health.data.etl.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coverage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    private Long year;
    private Long month;

    private Long population;
    private Long coverageNumber;
    private Long teams;
    private Double coveragePercent;

    public Double getTeamsDensity(){
        return (double) 100000 * teams / population;
    }
}
