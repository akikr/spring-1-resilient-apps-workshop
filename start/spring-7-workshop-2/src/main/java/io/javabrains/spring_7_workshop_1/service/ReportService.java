package io.javabrains.spring_7_workshop_1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    public String generateReport(String reportType) {
        log.info("Starting report generation: {}", reportType);

        try {
            Thread.sleep(10000); // Simulate 10 second intensive process
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("Completed report generation: {}", reportType);
        return "Report generated: " + reportType;
    }
}
