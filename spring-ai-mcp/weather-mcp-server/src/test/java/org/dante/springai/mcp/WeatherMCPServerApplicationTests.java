package org.dante.springai.mcp;

import lombok.extern.slf4j.Slf4j;
import org.dante.springai.mcp.service.WeatherService;
import org.dante.springai.mcp.util.JwtEd25519Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class WeatherMCPServerApplicationTests {

    @Autowired
    private WeatherService weatherService;

    @Test
    void lookupCity() {
        String result = weatherService.lookupCity("天水");
        log.info("=======> {}", result);
    }

    @Test
    void topCity() {
        String result = weatherService.topCity(3);
        log.info("=======> {}", result);
    }

    @Test
    void lookupCityScenicPOI() {
        String result = weatherService.lookupCityScenicPOI("天水");
        log.info("=======> {}", result);
    }

    @Test
    void weatherForecast() {
        String result = weatherService.weatherForecast("101160901", "3d");
        log.info("=======> {}", result);
    }

    @Test
    void realTimeWeather() {
        String result = weatherService.realTimeWeather("101160901");
        log.info("=======> {}", result);
    }

    @Test
    void realTimeWeatherWarning() {
        String result = weatherService.realTimeWeatherWarning("101160901");
        log.info("=======> {}", result);
    }

    @Test
    void weatherMinutePrecipitation() {
        // "lat":"34.57853","lon":"105.72500"
        String result = weatherService.weatherMinutePrecipitation("105.72,34.57");
        log.info("=======> {}", result);

    }

    @Test
    void weatherHistory() {
        String result = weatherService.weatherHistory("101160901", "20250620");
        log.info("=======> {}", result);
    }

    @Test
    void generateJwt() throws Exception {
        String privateKeyPath = "qweather-ed25519-private.pem";
        String kid = "K4WMJYTMGU";
        String sub = "242J3HRM2A";
        String jwt = JwtEd25519Util.generateJwt(kid, sub);
        log.info("=======> {}", jwt);
    }

}
