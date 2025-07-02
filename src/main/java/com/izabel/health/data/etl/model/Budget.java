package com.izabel.health.data.etl.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ou EAGER, se preferir carregar junto
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    private String state;
    private Long year;
    private Long bimonthly;

    private Double currentValue;
    private Double capitalValue;
}
