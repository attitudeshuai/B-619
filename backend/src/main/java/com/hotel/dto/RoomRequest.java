package com.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 客房请求 DTO
 */
@Data
public class RoomRequest {

    @NotBlank(message = "房间号不能为空")
    private String roomNumber;

    @NotBlank(message = "房型不能为空")
    private String roomType;

    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "楼层不能为空")
    @Positive(message = "楼层必须大于0")
    private Integer floor;

    private String description;
}
