package com.hotel.repository;

import com.hotel.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 入住登记数据访问层
 */
@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    List<CheckIn> findByStatus(String status);

    Optional<CheckIn> findFirstByRoomIdAndStatus(Long roomId, String status);
}
