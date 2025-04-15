package com.example.lpay.service;

import com.example.lpay.model.Tap;
import com.example.lpay.model.Trip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripProcessingService {
    private final TripCalculator tripCalculator;

    public Trip processCompletedTrip(Tap tapOn, Tap tapOff) {
        validateMatchingTap(tapOn, tapOff);
        BigDecimal fare = tripCalculator.getFare(tapOn.getStopId(), tapOff.getStopId());
        log.info("Processing completed trip: {} -> {} on Bus {} | Fare: {}", 
                 tapOn.getStopId(), tapOff.getStopId(), tapOn.getBusId(), fare);
        return createTrip(tapOn, tapOff, fare, "COMPLETED");
    }

    public Trip processIncompleteTrip(Tap tapOn) {
        BigDecimal maxFare = tripCalculator.getMaxFare(tapOn.getStopId());
        log.info("Processing incomplete trip: {} on Bus {} | Max fare: {}", 
                 tapOn.getStopId(), tapOn.getBusId(), maxFare);
        return createTrip(tapOn, null, maxFare, "INCOMPLETE (Tap-On Only)");
    }

    public Trip processIncompleteTapOffOnlyTrip(Tap tapOff) {
        BigDecimal maxFare = tripCalculator.getMaxFare(tapOff.getStopId());
        log.info("Processing incomplete trip: {} on Bus {} | Max fare: {}", 
                 tapOff.getStopId(), tapOff.getBusId(), maxFare);
        return createTrip(null, tapOff, maxFare, "INCOMPLETE (Tap-Off Only)");
    }

    public Trip processCancelledTrip(Tap tapOn, Tap tapOff) {
        validateMatchingTap(tapOn, tapOff);
        log.info("Processing cancelled trip: Stop {} | PAN {} | Bus {}", 
                 tapOn.getStopId(), tapOn.getPan(), tapOn.getBusId());
        return createTrip(tapOn, tapOff, BigDecimal.ZERO, "CANCELLED");
    }

    private void validateMatchingTap(Tap tapOn, Tap tapOff) {
        log.debug("Validating Tap Pair: Tap-On={} | Tap-Off={}", tapOn, tapOff);
    
        if (tapOn == null || tapOff == null ||
            !tapOn.getDateTimeUTC().isBefore(tapOff.getDateTimeUTC()) ||
            !tapOn.getPan().equals(tapOff.getPan()) ||
            !tapOn.getBusId().equals(tapOff.getBusId())) {
            throw new IllegalArgumentException("Invalid trip conditions.");
        }
    
        if (tapOn.getStopId().equals(tapOff.getStopId())) {
            log.info("Trip is CANCELLED: Tap-On and Tap-Off logged at the same stop.");
        }
    }
     

    private Trip createTrip(Tap tapOn, Tap tapOff, BigDecimal fare, String status) {
        return Trip.builder()
                .started(tapOn != null ? tapOn.getDateTimeUTC() : null)
                .finished(tapOff != null ? tapOff.getDateTimeUTC() : null)
                .durationSecs(tapOn != null && tapOff != null
                              ? Duration.between(tapOn.getDateTimeUTC(), tapOff.getDateTimeUTC()).getSeconds()
                              : null)
                .fromStopId(tapOn != null ? tapOn.getStopId() : null)
                .toStopId(tapOff != null ? tapOff.getStopId() : null)
                .chargeAmount(fare)
                .companyId(tapOn != null ? tapOn.getCompanyId() : tapOff.getCompanyId())
                .busId(tapOn != null ? tapOn.getBusId() : tapOff.getBusId())
                .pan(tapOn != null ? tapOn.getPan() : tapOff.getPan())
                .status(status)
                .build();
    }
}
