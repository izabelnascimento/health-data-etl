package com.izabel.health.data.etl.loader;

import com.izabel.health.data.etl.model.HealthCareVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HealthCareVisitRepository extends JpaRepository<HealthCareVisit, Long> {
    Optional<HealthCareVisit> findByYearAndMonthAndCity_Id(Long year, Long month, Long cityId);
}
