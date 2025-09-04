package com.kbsw.seasonthon.recommend.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OpenMeteoProvider {

    private final ObjectMapper om = new ObjectMapper();

    private RestTemplate rest() {
        var f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(5000);
        f.setReadTimeout(10000);
        return new RestTemplate(f);
    }

    public WeatherSnapshot fetch(double lat, double lon) {
        // 1) 실시간 날씨
        String wUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m,relative_humidity_2m,precipitation_probability,weather_code",
                lat, lon
        );

        // 2) 실시간 대기질 (PM10/PM2.5)
        String aUrl = String.format(
                "https://air-quality-api.open-meteo.com/v1/air-quality?latitude=%f&longitude=%f&hourly=pm10,pm2_5&timezone=auto",
                lat, lon
        );

        RestTemplate rt = rest();

        Double temperature = null;
        Long humidity = null;
        Double rainfallProb = null;
        Long weatherCode = null;

        // 날씨
        ResponseEntity<String> wRes = rt.getForEntity(wUrl, String.class);
        try {
            JsonNode w = om.readTree(wRes.getBody());
            JsonNode cur = w.path("current");
            if (!cur.isMissingNode()) {
                if (cur.hasNonNull("temperature_2m")) temperature = cur.get("temperature_2m").asDouble();
                if (cur.hasNonNull("relative_humidity_2m")) humidity = cur.get("relative_humidity_2m").asLong();
                if (cur.hasNonNull("precipitation_probability")) rainfallProb = cur.get("precipitation_probability").asDouble();
                if (cur.hasNonNull("weather_code")) weatherCode = cur.get("weather_code").asLong();
            }
        } catch (Exception ignored) {}

        // 대기질 (현 시각에 가장 근접한 값 사용)
        Long dust = null; // PM10 기준 (µg/m³)
        ResponseEntity<String> aRes = rt.getForEntity(aUrl, String.class);
        try {
            JsonNode a = om.readTree(aRes.getBody());
            JsonNode hourly = a.path("hourly");
            if (hourly.has("pm10") && hourly.has("time")) {
                // 가장 마지막(가장 최신) 값을 사용
                JsonNode pm10Arr = hourly.get("pm10");
                if (pm10Arr.isArray() && pm10Arr.size() > 0) {
                    double latest = pm10Arr.get(pm10Arr.size() - 1).asDouble();
                    dust = (long) Math.round((float) latest);
                }
            }
        } catch (Exception ignored) {}

        return WeatherSnapshot.builder()
                .weatherCode(weatherCode)
                .temperature(temperature)
                .humidity(humidity)
                .rainfallProbability(rainfallProb)
                .dust(dust)
                .build();
    }

    @lombok.Builder @lombok.Getter
    public static class WeatherSnapshot {
        private final Long weatherCode;         // Open-Meteo weather_code
        private final Double temperature;       // ℃
        private final Long humidity;            // %
        private final Double rainfallProbability; // %
        private final Long dust;                // PM10 (µg/m³)
    }
}