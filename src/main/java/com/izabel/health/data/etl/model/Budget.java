package com.izabel.health.data.etl.model;


import jakarta.persistence.*;

@Entity
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private String state;
    private int year;
    private int bimonthly;
    private Long value;
}
