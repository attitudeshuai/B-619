package com.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统配置请求
 */
@Data
public class SystemConfigRequest {

    @NotBlank(message = "配置值不能为空")
    private String configValue;

    private String description;
}
