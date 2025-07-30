package com.izabel.health.data.etl.common.loader;

import com.izabel.health.data.etl.common.dto.CityYearAggregationDTO;
import com.izabel.health.data.etl.common.dto.HealthCareVisitAggregationDTO;
import com.izabel.health.data.etl.common.model.HealthCareVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HealthCareVisitRepository extends JpaRepository<HealthCareVisit, Long> {
    Optional<HealthCareVisit> findByYearAndMonthAndCity_Id(Long year, Long month, Long cityId);
    List<HealthCareVisit> findByCityId(Long cityId);

    @Query(
        """
            SELECT 
                h.city.id as cityId, 
                SUM(COALESCE(h.individualVisit, 0) + COALESCE(h.dentistVisit, 0) + 
                    COALESCE(h.procedure, 0) + COALESCE(h.homeVisit, 0)) as total
            FROM HealthCareVisit h
            WHERE h.year = :year
            GROUP BY h.city.id
        """
    )
    List<HealthCareVisitAggregationDTO> findTotalVisitsByCityAndYear(@Param("year") Long year);

    @Query(
        """
            SELECT h.year as year,
                   SUM(COALESCE(h.individualVisit, 0) + COALESCE(h.dentistVisit, 0) +
                       COALESCE(h.procedure, 0) + COALESCE(h.homeVisit, 0)) as total
            FROM HealthCareVisit h
            WHERE h.city.id = :cityId
            GROUP BY h.year
            ORDER BY h.year
        """
    )
    List<CityYearAggregationDTO> findAnnualSumByCity(@Param("cityId") Long cityId);

}
