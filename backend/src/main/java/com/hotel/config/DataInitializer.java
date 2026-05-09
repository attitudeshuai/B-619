package com.hotel.config;

import com.hotel.entity.Admin;
import com.hotel.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 数据初始化 - 创建默认管理员
 */
@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public void run(String... args) {
        // 检查是否已有管理员
        if (adminRepository.count() == 0) {
            logger.info("初始化默认管理员账户...");

            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setNickname("系统管理员");

            adminRepository.save(admin);

            logger.info("默认管理员创建成功: admin / admin123");
        } else {
            logger.info("管理员已存在，跳过初始化");
        }
    }
}
