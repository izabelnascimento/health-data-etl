package com.izabel.health.data.etl.loader;

import com.izabel.health.data.etl.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> { }
