package com.izabel.health.data.etl.common.loader;

import com.izabel.health.data.etl.common.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByYear(Long year);
    List<Budget> findByYearAndCity_Id(Long year, Long cityId);
    List<Budget> findByCityId(Long cityId);
    Budget getFirstBudgetByCity_IdAndYearAndBimonthly(Long cityId, Long year, Long bimonthly);
}
