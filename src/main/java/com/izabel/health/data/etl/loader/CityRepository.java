package com.izabel.health.data.etl.loader;

import com.izabel.health.data.etl.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> { }
