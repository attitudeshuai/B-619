package com.hotel.config;

import com.hotel.entity.SystemConfig;
import com.hotel.repository.SystemConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统配置数据初始化
 */
@Component
@Order(4)
public class SystemConfigInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigInitializer.class);

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Override
    public void run(String... args) {
        if (systemConfigRepository.count() == 0) {
            logger.info("初始化系统配置数据...");

            List<SystemConfig> configs = new ArrayList<>();

            SystemConfig name = new SystemConfig();
            name.setConfigKey("hotel_name");
            name.setConfigValue("星河酒店");
            name.setDescription("酒店名称");
            configs.add(name);

            SystemConfig phone = new SystemConfig();
            phone.setConfigKey("hotel_phone");
            phone.setConfigValue("400-800-1234");
            phone.setDescription("酒店联系电话");
            configs.add(phone);

            SystemConfig address = new SystemConfig();
            address.setConfigKey("hotel_address");
            address.setConfigValue("上海市浦东新区星耀路88号");
            address.setDescription("酒店地址");
            configs.add(address);

            SystemConfig checkInTime = new SystemConfig();
            checkInTime.setConfigKey("checkin_time");
            checkInTime.setConfigValue("14:00");
            checkInTime.setDescription("标准入住时间");
            configs.add(checkInTime);

            SystemConfig checkOutTime = new SystemConfig();
            checkOutTime.setConfigKey("checkout_time");
            checkOutTime.setConfigValue("12:00");
            checkOutTime.setDescription("标准退房时间");
            configs.add(checkOutTime);

            systemConfigRepository.saveAll(configs);
            logger.info("系统配置初始化完成，共创建 {} 条配置", configs.size());
        } else {
            logger.info("系统配置已存在，跳过初始化");
        }
    }
}
