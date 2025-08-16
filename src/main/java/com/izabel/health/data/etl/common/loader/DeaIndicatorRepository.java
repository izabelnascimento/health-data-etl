package com.izabel.health.data.etl.common.loader;

import com.izabel.health.data.etl.common.model.DeaIndicator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeaIndicatorRepository extends JpaRepository<DeaIndicator, Long> {
    List<DeaIndicator> findByYearAndBimonthly(Long year, Long bimonthly);
}
