package com.izabel.health.data.etl.service;

import com.izabel.health.data.etl.dto.CoverageDTO;
import com.izabel.health.data.etl.extractor.CoverageExtractor;
import com.izabel.health.data.etl.source.Siops;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoverageETLService extends Siops{

    private final CoverageExtractor extractor;

    public List<CoverageDTO> fetchAndSaveCitiesBudget() {
        return extractor.extract();
    }
}

