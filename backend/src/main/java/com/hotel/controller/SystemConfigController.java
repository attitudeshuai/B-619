package com.hotel.controller;

import com.hotel.dto.ApiResponse;
import com.hotel.dto.SystemConfigRequest;
import com.hotel.entity.SystemConfig;
import com.hotel.service.SystemConfigService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 */
@RestController
@RequestMapping("/api/settings")
public class SystemConfigController {

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigController.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SystemConfig>>> getAllConfigs() {
        List<SystemConfig> configs = systemConfigService.getAllConfigs();
        return ResponseEntity.ok(ApiResponse.success(configs));
    }

    @PutMapping("/{key}")
    public ResponseEntity<ApiResponse<SystemConfig>> updateConfig(
            @PathVariable String key,
            @Valid @RequestBody SystemConfigRequest request) {
        logger.info("更新系统配置: {}", key);
        SystemConfig config = systemConfigService.updateConfig(key, request);
        return ResponseEntity.ok(ApiResponse.success("配置已更新", config));
    }
}
