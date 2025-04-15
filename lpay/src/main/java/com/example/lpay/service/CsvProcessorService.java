package com.example.lpay.service;

import com.example.lpay.model.Tap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvProcessorService {

    public List<Tap> loadTaps(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader().withTrim())) {

            log.info("Loading taps from file: {}", filePath);
            log.info("Detected headers: {}", parser.getHeaderMap().keySet());

            return parser.getRecords().stream()
                .map(this::mapRecordToTap)
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error reading CSV file: {}", filePath, e);
            throw new RuntimeException("Failed to load taps", e);
        }
    }

    private Tap mapRecordToTap(CSVRecord record) {
        try {
            return Tap.builder()
                .id(Long.parseLong(record.get("ID").trim()))
                .dateTimeUTC(LocalDateTime.parse(record.get("DateTimeUTC").trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                .tapType(record.get("TapType").trim())
                .stopId(record.get("StopId").trim())
                .companyId(record.get("CompanyId").trim())
                .busId(record.get("BusID").trim())
                .pan(record.get("PAN").trim())
                .build();
        } catch (Exception e) {
            log.error("Error parsing record: {}", record, e);
            throw new RuntimeException("Error parsing CSV record", e);
        }
    }
}
