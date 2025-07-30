package com.izabel.health.data.etl.common.loader;

import com.izabel.health.data.etl.common.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> { }
