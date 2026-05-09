package com.hotel.service;

import com.hotel.dto.LoginRequest;
import com.hotel.dto.LoginResponse;
import com.hotel.entity.Admin;
import com.hotel.repository.AdminRepository;
import com.hotel.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 认证服务
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 管理员登录
     */
    public LoginResponse login(LoginRequest request) {
        logger.info("用户尝试登录: {}", request.getUsername());

        Optional<Admin> adminOpt = adminRepository.findByUsername(request.getUsername());

        if (adminOpt.isEmpty()) {
            logger.warn("登录失败 - 用户不存在: {}", request.getUsername());
            return new LoginResponse(null, null, null, "用户名或密码错误");
        }

        Admin admin = adminOpt.get();

        // 验证密码（实际项目应使用 BCrypt 加密）
        if (!admin.getPassword().equals(request.getPassword())) {
            logger.warn("登录失败 - 密码错误: {}", request.getUsername());
            return new LoginResponse(null, null, null, "用户名或密码错误");
        }

        // 生成 JWT Token
        String token = jwtUtil.generateToken(admin.getUsername());

        logger.info("用户登录成功: {}", request.getUsername());
        return new LoginResponse(token, admin.getUsername(), admin.getNickname());
    }

    /**
     * 根据用户名获取管理员信息
     */
    public Admin getAdminByUsername(String username) {
        return adminRepository.findByUsername(username).orElse(null);
    }
}
