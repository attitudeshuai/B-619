package com.hotel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 入住请求
 */
@Data
public class CheckInRequest {

    @NotNull(message = "客房不能为空")
    private Long roomId;

    @NotNull(message = "会员不能为空")
    private Long memberId;

    private Long bookingId;

    private String remark;
}
