package com.izabel.health.data.etl.etl.transformer;

import com.izabel.health.data.etl.common.dto.BudgetDTO;
import com.izabel.health.data.etl.common.model.Budget;
import com.izabel.health.data.etl.common.model.City;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class BudgetTransformation {

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
        budget.setBimonthlyBudget(
                Objects.requireNonNullElse(budget.getCapitalValue(), 0.0)
                        + Objects.requireNonNullElse(budget.getCurrentValue(), 0.0)
        );
        return budget;
    }
}
