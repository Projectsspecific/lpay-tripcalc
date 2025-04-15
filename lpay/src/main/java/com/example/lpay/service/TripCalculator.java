package com.example.lpay.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "fare")
public class TripCalculator {
    private Map<String, Map<String, BigDecimal>> map;

    public BigDecimal getFare(String fromStop, String toStop) {
        return map.getOrDefault(fromStop, Map.of()).getOrDefault(toStop, BigDecimal.ZERO);
    }

    public BigDecimal getMaxFare(String stopId) {
        return map.getOrDefault(stopId, Map.of())
                  .values()
                  .stream()
                  .max(BigDecimal::compareTo)
                  .orElse(BigDecimal.ZERO);
    }
}
