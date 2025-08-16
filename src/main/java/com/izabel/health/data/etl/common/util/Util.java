package com.izabel.health.data.etl.common.util;

import java.util.List;
import java.util.Map;

public abstract class Util {
    public static final List<Long> COMMON_YEARS = List.of(2021L, 2022L, 2023L, 2024L);
    public static final List<Long> FIRST_BIMESTERS_ID = List.of(12L, 14L, 1L);
    public static final List<Long> ALL_BIMESTERS_ID = List.of(12L, 14L, 1L, 18L, 20L, 2L);

    public static final Map<Long, Long> BIMESTER_MAP = Map.of(
            12L, 1L,
            14L, 2L,
            1L,  3L,
            18L, 4L,
            20L, 5L,
            2L,  6L
    );

    public static Long parseBimester(Long bimonthly) {
        Long result = BIMESTER_MAP.get(bimonthly);
        if (result == null) {
            throw new IllegalArgumentException("Bimestre desconhecido: " + bimonthly);
        }
        return result;
    }

}
