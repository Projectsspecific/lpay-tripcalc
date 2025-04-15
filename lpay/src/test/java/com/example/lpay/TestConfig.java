package com.example.lpay;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.lpay.service.CsvProcessorService;

@Configuration
public class TestConfig {
    @Bean
    public CsvProcessorService csvProcessorService() {
        return new CsvProcessorService();
    }
}

