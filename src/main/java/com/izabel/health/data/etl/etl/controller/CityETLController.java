package com.izabel.health.data.etl.etl.controller;

import com.izabel.health.data.etl.etl.service.CityETLService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/etl/city")
@AllArgsConstructor
public class CityETLController {

    private final CityETLService cityETLService;

    @PostMapping("/collect")
    public int collectCitiesData() {
        return cityETLService.fetchAndSaveCities();
    }
}

