package com.izabel.health.data.etl.api.service;

import com.izabel.health.data.etl.common.loader.CityRepository;
import com.izabel.health.data.etl.common.model.City;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository repository;

    public List<City> getAll() {
        return repository.findAll();
    }
}

