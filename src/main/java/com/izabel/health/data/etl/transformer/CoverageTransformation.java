package com.izabel.health.data.etl.transformer;

import com.izabel.health.data.etl.dto.CoverageDTO;
import com.izabel.health.data.etl.mapper.CoverageMapper;
import com.izabel.health.data.etl.model.Coverage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CoverageTransformation {

    public List<Coverage> transform(List<CoverageDTO> coverageDTOS) {
        List<Coverage> coverages = new ArrayList<>();
        for (CoverageDTO coverageDTO : coverageDTOS) {
            Long month = Long.parseLong(coverageDTO.getNuComp().substring(0, 2));
            Long year = Long.parseLong(coverageDTO.getNuComp().substring(3, 7));
            coverages.add(CoverageMapper.toEntity(coverageDTO, year, month, getTeams(coverageDTO)));
        }
        return coverages;
    }

    private Long getTeams(CoverageDTO coverageDTO) {
        double teams = coverageDTO.getQtEsf()
                + (coverageDTO.getQtEap20() * 0.5)
                + (coverageDTO.getQtEap30() * 0.75)
                + coverageDTO.getQtEcr()
                + (coverageDTO.getQtEapp20() * 0.5)
                + (coverageDTO.getQtEap30() * 0.75)
                + coverageDTO.getQtEsfr();
        return Math.round(teams);
    }
}
