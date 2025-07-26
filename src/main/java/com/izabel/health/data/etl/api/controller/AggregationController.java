package com.izabel.health.data.etl.api.controller;

import com.izabel.health.data.etl.api.service.AggregationService;
import com.izabel.health.data.etl.dto.AggregationCityDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aggregation")
@AllArgsConstructor
public class AggregationController {

    private AggregationService aggregationService;

    @GetMapping("/{year}/{cityId}")
    public AggregationCityDTO aggregationByYear(@PathVariable Long year, @PathVariable Long cityId) throws Exception {
        return aggregationService.aggregate(cityId, year);
    }

    @GetMapping("/{year}")
    public List<AggregationCityDTO> aggregation(@PathVariable Long year) throws Exception {
        return aggregationService.aggregate(year);
    }
}
