package com.example.lpay;

import com.example.lpay.model.Tap;
import com.example.lpay.service.CsvProcessorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestConfig.class)
public class CsvProcessorServiceTest {

    @Autowired // Ensure proper injection
    private CsvProcessorService csvProcessorService;

    @Value("${test.tapsFilePath}")
    private String tapsFilePath;


    @Test
    public void testLoadTapsSuccess() {
        List<Tap> taps = csvProcessorService.loadTaps(tapsFilePath);

        assertFalse(taps.isEmpty(), "Tap list should not be empty");
        assertEquals("Stop1", taps.get(0).getStopId());
        assertEquals("4111111111111110", taps.get(0).getPan());
    }
}
