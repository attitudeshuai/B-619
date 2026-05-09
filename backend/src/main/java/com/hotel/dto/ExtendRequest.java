package com.hotel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 续住请求
 */
@Data
public class ExtendRequest {

    @NotNull(message = "续住天数不能为空")
    @Min(value = 1, message = "续住天数至少为1")
    private Integer extendDays;
}
