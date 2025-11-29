package io.javabrains.spring_7_workshop_1.controller;

import io.javabrains.spring_7_workshop_1.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/report")
    public String generateReport(@RequestParam(defaultValue = "sales") String type) {
        return reportService.generateReport(type);
    }
}
