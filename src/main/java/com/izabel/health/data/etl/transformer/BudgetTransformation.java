package com.izabel.health.data.etl.transformer;

import com.izabel.health.data.etl.dto.BudgetDTO;
import com.izabel.health.data.etl.model.Budget;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BudgetTransformation {

    public Budget transform(List<BudgetDTO> rawList) {
        Budget budget = new Budget();
        budget.setCity("261160");
        budget.setState("26");
        budget.setYear(2025);
        budget.setBimonthly(14);

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
