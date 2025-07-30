package com.izabel.health.data.etl.common.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class HealthCareVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    private Long year;
    private Long month;

    private Long individualVisit;
    private Long dentistVisit;
    private Long procedure;
    private Long homeVisit;

    public Long getVisits(){
        return individualVisit + dentistVisit + procedure + homeVisit;
    }
}
