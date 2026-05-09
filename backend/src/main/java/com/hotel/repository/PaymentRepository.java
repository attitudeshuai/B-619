package com.hotel.repository;

import com.hotel.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付记录数据访问层
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p where p.paymentTime >= :start and p.paymentTime <= :end")
    List<Payment> findByPaymentTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select function('date', p.paymentTime) as day, sum(p.amount) as total from Payment p " +
            "where p.paymentTime >= :start and p.paymentTime <= :end group by function('date', p.paymentTime)")
    List<Object[]> sumAmountGroupByDay(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
