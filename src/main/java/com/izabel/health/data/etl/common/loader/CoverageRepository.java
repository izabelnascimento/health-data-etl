package com.izabel.health.data.etl.common.loader;

import com.izabel.health.data.etl.common.model.Coverage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoverageRepository extends JpaRepository<Coverage, Long> {
    Optional<Coverage> findByYearAndMonthAndCity_Id(Long year, Long month, Long cityId);
}
