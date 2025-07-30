package com.izabel.health.data.etl.etl.controller;

import com.izabel.health.data.etl.api.service.CityETLService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/etl/city")
@AllArgsConstructor
public class CityETLController {

//    TODO POR ENQUANTO É UM CONTROLLER, MAS A IDEIA É QUE SEJA UM SCHEDULER MENSAL

    private final CityETLService cityETLService;

    @GetMapping
    public int collectCitiesData() {
        return cityETLService.fetchAndSaveCities();
    }
}

