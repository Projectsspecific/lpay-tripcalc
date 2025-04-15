package com.example.lpay.controller;

import com.example.lpay.model.Tap;
import com.example.lpay.model.Trip;
import com.example.lpay.service.CsvProcessorService;
import com.example.lpay.service.CsvWriterService;
import com.example.lpay.service.TripProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {
    private final CsvProcessorService csvProcessorService;
    private final TripProcessingService tripProcessingService;
    private final CsvWriterService csvWriterService;

    @PostMapping("/process")
    public ResponseEntity<String> processTrips() {
        try {
            List<Tap> taps = csvProcessorService.loadTaps(getClass().getClassLoader().getResource("taps.csv").getFile());
            Map<String, Tap> activeTrips = new HashMap<>();

            // Sort taps by timestamp
            taps.sort(Comparator.comparing(Tap::getDateTimeUTC));

            // Process trips using Streams
            List<Trip> trips = taps.stream()
                .map(tap -> processTapEvent(activeTrips, tap))
                .filter(Objects::nonNull) // Remove null entries
                .collect(Collectors.toList());

            activeTrips.values().forEach(tap -> trips.add(tripProcessingService.processIncompleteTrip(tap)));

            // Write trips to CSV file
            csvWriterService.writeTripsToCsv(trips);

            return ResponseEntity.ok("Trips processed successfully.Check the output file for details.");
        } catch (Exception e) {
            log.error("Error processing trips", e);
            return ResponseEntity.status(500).body("An error occurred while processing trips." + e);
        }
    }

    private Trip processTapEvent(Map<String, Tap> activeTrips, Tap tap) {
        String pan = tap.getPan();
        if ("ON".equalsIgnoreCase(tap.getTapType())) {
            activeTrips.put(pan, tap);
            return null;
        } else if ("OFF".equalsIgnoreCase(tap.getTapType())) {
            Tap tapOn = activeTrips.remove(pan);
            if (tapOn != null) {
                return determineTripType(tapOn, tap);
            } else {
                return tripProcessingService.processIncompleteTapOffOnlyTrip(tap);
            }
        }
        return null;
    }

    private Trip determineTripType(Tap tapOn, Tap tapOff) {
        if (!tapOn.getBusId().equals(tapOff.getBusId())) {
            return tripProcessingService.processIncompleteTrip(tapOn);
        } else if (tapOn.getStopId().equals(tapOff.getStopId())) {
            return tripProcessingService.processCancelledTrip(tapOn, tapOff);
        }
        return tripProcessingService.processCompletedTrip(tapOn, tapOff);
    }
}
