package com.izabel.health.data.etl.loader;

import com.izabel.health.data.etl.model.Coverage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoverageRepository extends JpaRepository<Coverage, Long> { }
