package com.hotel.repository;

import com.hotel.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 预订数据访问层
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStatus(String status);

    List<Booking> findByMemberId(Long memberId);

    List<Booking> findByRoomId(Long roomId);

    @Query("select b from Booking b where b.checkInDate >= :start and b.checkOutDate <= :end")
    List<Booking> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
