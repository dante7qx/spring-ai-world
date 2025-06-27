package org.dante.springai.mcp.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dante.springai.mcp.util.JwtEd25519Util;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WebClient webClient;
    private static final String KID = "K4WMJYTMGU";
    private static final String SUB = "242J3HRM2A";
    private String JWT_TOKEN;

    @PostConstruct
    void init() throws Exception {
        JWT_TOKEN = "Bearer " + JwtEd25519Util.generateJwt(KID, SUB);
    }

    @Tool(name = "lookupCity", description = "获取指定城市的位置信息")
    public String lookupCity(String city) {
        return webClient.get()
                .uri("/geo/v2/city/lookup?location={city}", city)
                .header("Authorization", JWT_TOKEN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Tool(name = "chinaTopCity", description = "获取中国指定数量的热门城市信息")
    public String topCity(int number) {
        return webClient.get()
                .uri("/geo/v2/city/top?number={number}&range=cn", number)
                .header("Authorization", JWT_TOKEN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Tool(name = "lookupCityScenic", description = "获取指定城市的景点信息")
    public String lookupCityScenicPOI(String city) {
        return webClient.get()
               .uri("/geo/v2/poi/lookup?type=scenic&location={city}", city)
               .header("Authorization", JWT_TOKEN)
               .retrieve()
               .bodyToMono(String.class)
               .block();
    }

    @Tool(name = "weatherForecast", description = "获取指定城市的未来3-30天的天气预报信息")
    public String weatherForecast(String locationId, String days) {
        return webClient.get()
                .uri("/v7/weather/{days}?location={locationId}", days, locationId)
                .header("Authorization", JWT_TOKEN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Tool(name = "realTimeWeather", description = "获取指定城市的实时天气信息")
    public String realTimeWeather(String locationId) {
        return webClient.get()
                .uri("/v7/weather/now?location={locationId}", locationId)
                .header("Authorization", JWT_TOKEN)
               .retrieve()
               .bodyToMono(String.class)
               .block();
    }

    @Tool(name = "realTimeWeatherWarning", description = "获取指定城市的实时天气灾害预警信息")
    public String realTimeWeatherWarning(String locationId) {
        return webClient.get()
                .uri("/v7/warning/now?location={locationId}", locationId)
                .header("Authorization", JWT_TOKEN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * 获取指定城市1公里精度的未来2小时每5分钟降雨预报数据
     *
     * @param location 地区的经纬度，格式为经度,纬度（十进制，最多支持小数点后两位）。例如：116.39,39.90
     * @return 未来2小时每5分钟降雨预报数据
     */
    @Tool(name = "weatherMinutePrecipitation", description = "获取指定城市1公里精度的未来2小时每5分钟降雨预报数据(经纬度坐标，格式为经度,纬度，保留2位小数)")
    public String weatherMinutePrecipitation(String location) {
        return webClient.get()
                .uri("/v7/minutely/5m?location={location}", location)
                .header("Authorization", JWT_TOKEN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


    /**
     * 获取指定城市指定日期的最近10天的天气信息
     *
     * @param locationId 位置ID，可通过城市定位接口获取
     * @param date 日期，最多可选择最近10天（不包含今天）的数据, 格式：yyyyMMdd
     * @return 最近10天的天气历史
     */
    @Tool(name = "weatherHistory", description = "获取指定城市指定日期的最近10天的天气信息")
    public String weatherHistory(String locationId, String date) {
        return webClient.get()
                .uri("/v7/historical/weather?location={locationId}&date={date}", locationId, date)
                .header("Authorization", JWT_TOKEN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


}
