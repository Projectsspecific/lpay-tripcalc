package com.example.lpay.service;

import com.example.lpay.model.Trip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvWriterService {

    @Value("${output.tripsFilePath}") // Loading output file path from application.yml
    private String tripsFilePath;

    public void writeTripsToCsv(List<Trip> trips) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tripsFilePath));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                 "Started", "Finished", "DurationSecs", "FromStopId", "ToStopId",
                 "ChargeAmount", "CompanyId", "BusID", "PAN", "Status"))) {

            trips.forEach(trip -> writeTripRecord(csvPrinter, trip));

            log.info("Trips successfully written to file: {}", tripsFilePath);
        } catch (IOException e) {
            log.error("Error writing trips to CSV", e);
            throw new RuntimeException("Failed to write trips", e);
        }
    }

    private void writeTripRecord(CSVPrinter csvPrinter, Trip trip) {
        try {
            csvPrinter.printRecord(
                trip.getStarted(), trip.getFinished(), trip.getDurationSecs(),
                trip.getFromStopId(), trip.getToStopId(), trip.getChargeAmount(),
                trip.getCompanyId(), trip.getBusId(), trip.getPan(), trip.getStatus()
            );
        } catch (IOException e) {
            log.error("Error writing trip record: {}", trip, e);
        }
    }
}
