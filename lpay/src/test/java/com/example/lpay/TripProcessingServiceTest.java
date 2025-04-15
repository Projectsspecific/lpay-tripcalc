package com.example.lpay;

import com.example.lpay.model.Tap;
import com.example.lpay.model.Trip;
import com.example.lpay.service.TripCalculator;
import com.example.lpay.service.TripProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class TripProcessingServiceTest {

    private TripProcessingService tripProcessingService;
    private TripCalculator tripCalculator;

    @BeforeEach
    public void setup() {
        tripCalculator = mock(TripCalculator.class);
        tripProcessingService = new TripProcessingService(tripCalculator);
    }

    @Test
    public void testProcessCompletedTrip() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(30);
        LocalDateTime endTime = LocalDateTime.now().minusMinutes(5); // Ensure valid time sequence
    
        Tap tapOn = new Tap(1L, startTime, "ON", "Stop1", "Company1", "Bus1", "12345");
        Tap tapOff = new Tap(2L, endTime, "OFF", "Stop2", "Company1", "Bus1", "12345"); // Ensure same Bus ID
    
        when(tripCalculator.getFare("Stop1", "Stop2")).thenReturn(BigDecimal.valueOf(3.25));
    
        Trip trip = tripProcessingService.processCompletedTrip(tapOn, tapOff);
    
        assertEquals("COMPLETED", trip.getStatus());
    }
    

@Test
public void testProcessCancelledTrip() {
        Tap tapOn = new Tap(1L, LocalDateTime.now(), "ON", "Stop1", "Company1", "Bus1", "12345");
        Tap tapOff = new Tap(2L, LocalDateTime.now().plusMinutes(2), "OFF", "Stop1", "Company1", "Bus1", "12345");

        Trip trip = tripProcessingService.processCancelledTrip(tapOn, tapOff);

        assertAll(
            () -> assertEquals("CANCELLED", trip.getStatus()),
            () -> assertEquals("Stop1", trip.getFromStopId()),
            () -> assertEquals(BigDecimal.ZERO, trip.getChargeAmount())
        );
    }

   
    @Test
    public void testProcessMissedTapOff() {
        Tap tapOn1 = new Tap(1L, LocalDateTime.now().minusMinutes(30), "ON", "Stop1", "Company1", "Bus1", "1111");
        Tap tapOn2 = new Tap(2L, LocalDateTime.now().minusMinutes(20), "ON", "Stop2", "Company1", "Bus1", "2222");

        when(tripCalculator.getMaxFare("Stop1")).thenReturn(BigDecimal.valueOf(7.30));
        when(tripCalculator.getMaxFare("Stop2")).thenReturn(BigDecimal.valueOf(5.50));

        Trip trip1 = tripProcessingService.processIncompleteTrip(tapOn1);
        Trip trip2 = tripProcessingService.processIncompleteTrip(tapOn2);

        assertAll(
            () -> assertEquals("INCOMPLETE (Tap-On Only)", trip1.getStatus()),
            () -> assertEquals(BigDecimal.valueOf(7.30), trip1.getChargeAmount()),
            () -> assertEquals("INCOMPLETE (Tap-On Only)", trip2.getStatus()),
            () -> assertEquals(BigDecimal.valueOf(5.50), trip2.getChargeAmount())
        );
    }
}
