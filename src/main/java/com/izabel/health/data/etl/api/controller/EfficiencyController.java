package com.izabel.health.data.etl.api.controller;

import com.izabel.health.data.etl.api.service.EfficiencyService;
import com.izabel.health.data.etl.dto.EfficiencyCityDTO;
import com.izabel.health.data.etl.dto.RankedEfficiencyCityDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/efficiency")
@AllArgsConstructor
public class EfficiencyController {

    private EfficiencyService efficiencyService;

    @GetMapping("/{year}/{cityId}")
    public EfficiencyCityDTO efficiencyByCity(@PathVariable Long year, @PathVariable Long cityId) {
        return efficiencyService.efficiency(cityId, year);
    }

    @GetMapping("/{year}")
    public List<EfficiencyCityDTO> efficiency(@PathVariable Long year) {
        return efficiencyService.efficiency(year);
    }
}
