package com.example.lpay;

import com.example.lpay.model.Trip;
import com.example.lpay.service.CsvWriterService;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CsvWriterServiceTest {

    @InjectMocks
    private CsvWriterService csvWriterService;

    private final String testFilePath = "output_trips.csv";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(csvWriterService, "tripsFilePath", testFilePath);
    }

    @Test
    void testWriteTripsToCsv_Success() throws IOException {
        // Mock trip data
        Trip trip = Trip.builder()
            .started(LocalDateTime.now().minusMinutes(20))
            .finished(LocalDateTime.now())
            .durationSecs(1200L)
            .fromStopId("Stop1")
            .toStopId("Stop2")
            .chargeAmount(BigDecimal.valueOf(3.50))
            .companyId("Company1")
            .busId("Bus1")
            .pan("1234567890123456")
            .status("COMPLETED")
            .build();

        List<Trip> trips = List.of(trip);

        csvWriterService.writeTripsToCsv(trips);

        // Verify the CSV file content
        try (FileReader reader = new FileReader(testFilePath);
             CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {

            CSVRecord record = parser.iterator().next();

            assertEquals("Stop1", record.get("FromStopId"));
            assertEquals("Stop2", record.get("ToStopId"));
            assertEquals("3.50", new BigDecimal(record.get("ChargeAmount")).setScale(2).toString());
            assertEquals("COMPLETED", record.get("Status"));
        }
    }

}
