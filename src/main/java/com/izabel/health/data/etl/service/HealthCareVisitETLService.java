package com.izabel.health.data.etl.service;

import com.izabel.health.data.etl.extractor.HealthCareVisitExtractor;
import com.izabel.health.data.etl.loader.HealthCareVisitRepository;
import com.izabel.health.data.etl.model.HealthCareVisit;
import com.izabel.health.data.etl.transformer.HealthCareVisitTransformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthCareVisitETLService {

    private final HealthCareVisitRepository loading;
    private final HealthCareVisitExtractor extractor;
    private final HealthCareVisitTransformation transformation;

    public int fetchAndSaveCitiesBudget() throws IOException {
        extractor.batchExtract();
        List<HealthCareVisit> healthCareVisits = transformation.batchTransformation();
        return loading.saveAll(healthCareVisits).size();
    }

}

