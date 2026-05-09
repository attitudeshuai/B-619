package com.hotel.controller;

import com.hotel.dto.AdminRequest;
import com.hotel.dto.ApiResponse;
import com.hotel.entity.Admin;
import com.hotel.service.AdminService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Admin>>> getAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Admin> admins = adminService.getAdmins(pageable);
        return ResponseEntity.ok(ApiResponse.success(admins));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Admin>> createAdmin(@Valid @RequestBody AdminRequest request) {
        logger.info("创建管理员: {}", request.getUsername());
        Admin admin = adminService.createAdmin(request);
        return ResponseEntity.ok(ApiResponse.success("创建成功", admin));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Admin>> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminRequest request) {
        logger.info("更新管理员: ID={}", id);
        Admin admin = adminService.updateAdmin(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新成功", admin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}
