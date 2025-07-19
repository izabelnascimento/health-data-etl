package com.izabel.health.data.etl.transformer;

import com.izabel.health.data.etl.dto.BudgetDTO;
import com.izabel.health.data.etl.model.Budget;
import com.izabel.health.data.etl.model.City;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HealthCareVisitTransformation {

    public Budget transform(List<BudgetDTO> rawList) {
        Budget budget = new Budget();
        budget.setCity(City.builder().id(261160L).build());
        budget.setState("26");
        budget.setYear(2025L);
        budget.setBimonthly(14L);

        for (BudgetDTO dto : rawList) {
            if ("301 - Atenção Básica - Corrente".equals(dto.getDsItem())) {
                budget.setCurrentValue(dto.getVl_coluna10());
            } else if ("301 - Atenção Básica - Capital".equals(dto.getDsItem())) {
                budget.setCapitalValue(dto.getVl_coluna10());
            }
        }
        return budget;
    }

    public Budget transform(List<BudgetDTO> rawList, Long cityId, Long stateId, Long year, Long bimonthly) {
        Budget budget = Budget.builder()
                .city(City.builder().id(cityId).build())
                .state(stateId.toString())
                .year(year)
                .bimonthly(bimonthly)
                .build();

        for (BudgetDTO dto : rawList) {
            if ("301 - Atenção Básica - Corrente".equals(dto.getDsItem())) {
                budget.setCurrentValue(dto.getVl_coluna10());
            } else if ("301 - Atenção Básica - Capital".equals(dto.getDsItem())) {
                budget.setCapitalValue(dto.getVl_coluna10());
            }
        }
        return budget;
    }
}
