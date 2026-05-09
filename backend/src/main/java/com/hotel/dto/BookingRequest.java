package com.hotel.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 预订请求
 */
@Data
public class BookingRequest {

    @NotNull(message = "客房不能为空")
    private Long roomId;

    @NotNull(message = "会员不能为空")
    private Long memberId;

    @NotNull(message = "入住日期不能为空")
    @FutureOrPresent(message = "入住日期不能早于今天")
    private LocalDate checkInDate;

    @NotNull(message = "退房日期不能为空")
    @Future(message = "退房日期必须是未来日期")
    private LocalDate checkOutDate;

    @PositiveOrZero(message = "押金不能小于0")
    private BigDecimal deposit;

    private String remark;
}
