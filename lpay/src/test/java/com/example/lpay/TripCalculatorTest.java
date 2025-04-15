package com.example.lpay;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.lpay.service.TripCalculator;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TripCalculatorTest {

    private TripCalculator tripCalculator;

    @BeforeEach
    void setUp() {
        tripCalculator = new TripCalculator();

        // Mock fare map
        Map<String, Map<String, BigDecimal>> mockFareMap = Map.of(
            "Stop1", Map.of("Stop2", BigDecimal.valueOf(2.50), "Stop3", BigDecimal.valueOf(3.75)),
            "Stop2", Map.of("Stop3", BigDecimal.valueOf(1.80)),
            "Stop3", Map.of("Stop1", BigDecimal.valueOf(3.50), "Stop2", BigDecimal.valueOf(2.00))
        );

        // Inject mock data into tripCalculator
        ReflectionTestUtils.setField(tripCalculator, "map", mockFareMap);
    }

    @Test
    void testGetFare_ValidRoute() {
        assertEquals(BigDecimal.valueOf(2.50), tripCalculator.getFare("Stop1", "Stop2"));
        assertEquals(BigDecimal.valueOf(3.75), tripCalculator.getFare("Stop1", "Stop3"));
    }

    @Test
    void testGetFare_InvalidRoute() {
        assertEquals(BigDecimal.ZERO, tripCalculator.getFare("Stop1", "StopX")); // Nonexistent stop
        assertEquals(BigDecimal.ZERO, tripCalculator.getFare("StopX", "Stop1")); // Completely unknown stop
    }

    @Test
    void testGetMaxFare_ValidStop() {
        assertEquals(BigDecimal.valueOf(3.75), tripCalculator.getMaxFare("Stop1"));
        assertEquals(BigDecimal.valueOf(3.5), tripCalculator.getMaxFare("Stop3"));
    }

    @Test
    void testGetMaxFare_UnknownStop() {
        assertEquals(BigDecimal.ZERO, tripCalculator.getMaxFare("StopX"));
    }
}
