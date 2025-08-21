package com.izabel.health.data.etl.api.controller;

import com.izabel.health.data.etl.api.service.CityService;
import com.izabel.health.data.etl.common.model.City;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/city")
@AllArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping
    public List<City> getAll() {
        return cityService.getAll();
    }
}

