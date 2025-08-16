package com.izabel.health.data.etl.common.loader;

import com.izabel.health.data.etl.common.model.DeaIndicator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeaIndicatorRepository extends JpaRepository<DeaIndicator, Long> {
}
