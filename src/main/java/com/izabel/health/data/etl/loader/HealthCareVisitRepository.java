package com.izabel.health.data.etl.loader;

import com.izabel.health.data.etl.model.HealthCareVisit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthCareVisitRepository extends JpaRepository<HealthCareVisit, Long> {
}
