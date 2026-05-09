package com.hotel.controller;

import com.hotel.dto.ApiResponse;
import com.hotel.dto.LoginRequest;
import com.hotel.dto.LoginResponse;
import com.hotel.entity.Admin;
import com.hotel.service.AuthService;
import com.hotel.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("服务运行正常"));
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        logger.info("收到登录请求: {}", request.getUsername());

        LoginResponse response = authService.login(request);

        if (response.getToken() == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(401, response.getMessage()));
        }

        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserInfo(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "未提供有效的认证令牌"));
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "认证令牌已失效"));
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Admin admin = authService.getAdminByUsername(username);

        if (admin == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "用户不存在"));
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", admin.getId());
        userInfo.put("username", admin.getUsername());
        userInfo.put("nickname", admin.getNickname());

        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }
}
