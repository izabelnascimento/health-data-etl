package com.izabel.health.data.etl.service;

import com.izabel.health.data.etl.extractor.CityExtractor;
import com.izabel.health.data.etl.loader.CityRepository;
import com.izabel.health.data.etl.model.City;
import com.izabel.health.data.etl.transformer.CityTransformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityETLService {

    private final CityRepository repository;
    private final CityExtractor extractor;
    private final CityTransformation transformation;

    public int fetchAndSaveCities() {
        var response = extractor.extract();
        List<City> cities = transformation.transform(response);

        return repository.saveAll(cities).size();
    }
}

