package com.izabel.health.data.etl.service;

import com.izabel.health.data.etl.dto.CoverageDTO;
import com.izabel.health.data.etl.extractor.CoverageExtractor;
import com.izabel.health.data.etl.loader.CityRepository;
import com.izabel.health.data.etl.loader.CoverageRepository;
import com.izabel.health.data.etl.model.City;
import com.izabel.health.data.etl.model.Coverage;
import com.izabel.health.data.etl.source.Siops;
import com.izabel.health.data.etl.transformer.CoverageTransformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoverageETLService extends Siops{

    private final CoverageExtractor extractor;
    private final CoverageTransformation transformation;
    private final CoverageRepository loading;
    private final CityRepository cityRepository;

    public int collectPECitiesCoverageData() {
        log.info("Starting coverage collect");
        List<City> cities = cityRepository.findAll();
        int loaded = 0;
        for (City city : cities) {
            Long cityId = city.getId();
            List<CoverageDTO> extract = extractor.extract(cityId);
            List<Coverage> transformed = transformation.transform(extract);
            loaded = loaded + loading.saveAll(transformed).size();
        }
        log.info("Finished coverage collect");
        return loaded;
    }
}

