package com.izabel.health.data.etl.scheduler;

import com.izabel.health.data.etl.service.CityETLService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/city")
@AllArgsConstructor
public class CityController {

//    TODO POR ENQUANTO É UM CONTROLLER, MAS A IDEIA É QUE SEJA UM SCHEDULER MENSAL

    private final CityETLService cityETLService;

    @PostMapping("/cities-pe")
    public int collectCitiesData() {
        return cityETLService.fetchAndSaveCities();
    }
}

