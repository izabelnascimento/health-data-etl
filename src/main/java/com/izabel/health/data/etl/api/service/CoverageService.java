package com.izabel.health.data.etl.api.service;

import com.izabel.health.data.etl.common.loader.CoverageRepository;
import com.izabel.health.data.etl.common.model.Coverage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoverageService {

    private final CoverageRepository coverageRepository;

    public Coverage getCoverage(Long cityId, Long year, Long month) {
        return coverageRepository.findByYearAndMonthAndCity_Id(year, month, cityId).orElseThrow();
    }

}

