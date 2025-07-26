package com.izabel.health.data.etl.service;

import com.izabel.health.data.etl.extractor.HealthCareVisitExtractor;
import com.izabel.health.data.etl.loader.HealthCareVisitRepository;
import com.izabel.health.data.etl.source.Sisab;
import com.izabel.health.data.etl.transformer.HealthCareVisitTransformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthCareVisitETLService {

    private final HealthCareVisitRepository loading;
    private final HealthCareVisitExtractor extractor;
    private final HealthCareVisitTransformation transformation;

    public Long collectPECitiesHealthCareVisitData() throws IOException {
        extractor.batchExtract();
        for (Long production: Sisab.PRODUCTION) {
            loading.saveAll(transformation.batchTransformation(production));
        }
        return loading.count();
    }

}

