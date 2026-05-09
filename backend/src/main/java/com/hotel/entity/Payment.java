package com.hotel.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体
 */
@Data
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "check_in_id", nullable = false)
    private Long checkInId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod; // 现金、微信、支付宝、银行卡

    @Column(name = "payment_time", nullable = false)
    private LocalDateTime paymentTime;

    @Column(length = 500)
    private String remark;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.paymentTime == null) {
            this.paymentTime = LocalDateTime.now();
        }
    }
}
