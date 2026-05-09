package com.hotel.service;

import com.hotel.dto.AdminRequest;
import com.hotel.entity.Admin;
import com.hotel.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理员服务
 */
@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private AdminRepository adminRepository;

    public Page<Admin> getAdmins(Pageable pageable) {
        return adminRepository.findAll(pageable);
    }

    public Admin getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));
    }

    @Transactional
    public Admin createAdmin(AdminRequest request) {
        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setPassword(request.getPassword());
        admin.setNickname(request.getNickname());

        Admin saved = adminRepository.save(admin);
        logger.info("创建管理员: {}", saved.getUsername());
        return saved;
    }

    @Transactional
    public Admin updateAdmin(Long id, AdminRequest request) {
        Admin admin = getAdminById(id);

        if (!admin.getUsername().equals(request.getUsername())
                && adminRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        admin.setUsername(request.getUsername());
        admin.setPassword(request.getPassword());
        admin.setNickname(request.getNickname());

        Admin updated = adminRepository.save(admin);
        logger.info("更新管理员: {}", updated.getUsername());
        return updated;
    }

    @Transactional
    public void deleteAdmin(Long id) {
        if (adminRepository.count() <= 1) {
            throw new RuntimeException("至少保留一个管理员账号");
        }

        Admin admin = getAdminById(id);
        adminRepository.delete(admin);
        logger.info("删除管理员: {}", admin.getUsername());
    }
}
