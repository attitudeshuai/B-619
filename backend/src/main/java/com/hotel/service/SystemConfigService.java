package com.hotel.service;

import com.hotel.dto.SystemConfigRequest;
import com.hotel.entity.SystemConfig;
import com.hotel.repository.SystemConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统配置服务
 */
@Service
public class SystemConfigService {

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigService.class);

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    public List<SystemConfig> getAllConfigs() {
        return systemConfigRepository.findAll();
    }

    public SystemConfig getConfig(String key) {
        return systemConfigRepository.findByConfigKey(key)
                .orElseThrow(() -> new RuntimeException("配置不存在"));
    }

    @Transactional
    public SystemConfig updateConfig(String key, SystemConfigRequest request) {
        SystemConfig config = systemConfigRepository.findByConfigKey(key)
                .orElseGet(SystemConfig::new);

        config.setConfigKey(key);
        config.setConfigValue(request.getConfigValue());
        config.setDescription(request.getDescription());

        SystemConfig saved = systemConfigRepository.save(config);
        logger.info("更新系统配置: {}", key);
        return saved;
    }
}
