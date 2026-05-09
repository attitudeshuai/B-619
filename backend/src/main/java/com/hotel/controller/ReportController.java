package com.hotel.controller;

import com.hotel.dto.ApiResponse;
import com.hotel.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 报表控制器
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRevenueReport(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(defaultValue = "day") String period) {

        List<Map<String, Object>> report = reportService.getRevenueReport(start, end, period);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/occupancy")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOccupancyReport() {
        Map<String, Object> report = reportService.getOccupancyReport();
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMemberReport(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        List<Map<String, Object>> report = reportService.getMemberConsumptionReport(start, end);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportRevenueReport(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(defaultValue = "day") String period) {

        List<Map<String, Object>> report = reportService.getRevenueReport(start, end, period);
        StringBuilder builder = new StringBuilder();
        builder.append("周期,收入\n");
        for (Map<String, Object> item : report) {
            builder.append(item.get("period")).append(",").append(item.get("total")).append("\n");
        }
        byte[] content = builder.toString().getBytes(StandardCharsets.UTF_8);

        logger.info("导出收入报表: {} - {}, 周期={}", start, end, period);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(content);
    }
}
