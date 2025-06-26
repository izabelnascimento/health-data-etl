package com.izabel.health.data.etl.repository;

import com.izabel.health.data.etl.model.Value;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValorRepository extends JpaRepository<Value, Long> {}
