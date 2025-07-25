package com.izabel.health.data.etl.transformer;

import com.izabel.health.data.etl.loader.CityRepository;
import com.izabel.health.data.etl.loader.HealthCareVisitRepository;
import com.izabel.health.data.etl.model.City;
import com.izabel.health.data.etl.model.HealthCareVisit;
import com.izabel.health.data.etl.source.Sisab;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.izabel.health.data.etl.source.Sisab.parseMonth;

@Service
@AllArgsConstructor
@Slf4j
public class HealthCareVisitTransformation {

    private final CityRepository cityRepository;
    private final HealthCareVisitRepository healthCareVisitRepository;

    public List<HealthCareVisit> batchTransformation(Long production) {
        log.info("Iniciando transformação de dados production {}", production);
        List<HealthCareVisit> healthCareVisits = new ArrayList<>();
        for (Long year : Sisab.YEARS) {
            healthCareVisits.addAll(batchTransformation(year, production));
        }
        return healthCareVisits;
    }

    public List<HealthCareVisit> batchTransformation(Long year, Long production) {
        List<HealthCareVisit> visits = new ArrayList<>();
        Path path = Paths.get("src/main/resources/sisab/" + year + "_production_" + production + ".csv");

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            List<String> headers = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Uf;Ibge;Municipio")) {
                    headers = Arrays.asList(line.split(";"));
                    break;
                }
            }

            if (headers.isEmpty()) {
                throw new RuntimeException("Cabeçalho não encontrado no arquivo CSV.");
            }

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                Long ibgeCode = Long.valueOf(parts[1]);
                Optional<City> cityOpt = cityRepository.findById(ibgeCode);
                if (cityOpt.isEmpty()) continue;

                City city = cityOpt.get();

                for (int i = 3; i < parts.length; i++) {
                    String header = headers.get(i);
                    String valueStr =
                            parts[i].replace(".", "").replace(",", "").trim();
                    if (valueStr.isEmpty()) continue;
                    visits.add(createVisit(header, valueStr, city, year, production));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o CSV", e);
        }

        return visits;
    }

    private HealthCareVisit createVisit(String header, String valueStr, City city, Long year, Long production) {
        try {
            long value = Long.parseLong(valueStr);
            int month = parseMonth(header);
            Long monthLong = (long) month;

            Optional<HealthCareVisit> existingVisitOpt =
                    healthCareVisitRepository.findByYearAndMonthAndCity_Id(year, monthLong, city.getId());

            HealthCareVisit visit = existingVisitOpt.orElseGet(HealthCareVisit::new);
            visit.setCity(city);
            visit.setMonth(monthLong);
            visit.setYear(year);

            switch (production.intValue()) {
                case 4:
                    visit.setIndividualVisit(value);
                    break;
                case 5:
                    visit.setDentistVisit(value);
                    break;
                case 7:
                    visit.setProcedure(value);
                    break;
                case 8:
                    visit.setHomeVisit(value);
                    break;
                default:
                    log.warn("Production code {} not recognized. Value {} ignored.", production, valueStr);
                    break;
            }
            return visit;
        } catch (NumberFormatException e) {
            log.warn("Ignoring invalid value: {}", valueStr);
            return null;
        }
    }
}

