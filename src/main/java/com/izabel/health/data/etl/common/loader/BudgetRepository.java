package com.izabel.health.data.etl.common.loader;

import com.izabel.health.data.etl.common.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByYear(Long year);
    List<Budget> findByCityId(Long cityId);
}
