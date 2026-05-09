package com.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 退房结算请求
 */
@Data
public class CheckOutRequest {

    @NotNull(message = "结算金额不能为空")
    @PositiveOrZero(message = "结算金额不能小于0")
    private BigDecimal amount;

    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;

    private String remark;
}
