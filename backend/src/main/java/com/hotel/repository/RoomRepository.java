package com.hotel.repository;

import com.hotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 客房数据访问层
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * 根据房间号查找客房
     */
    Optional<Room> findByRoomNumber(String roomNumber);

    /**
     * 根据状态查找客房
     */
    List<Room> findByStatus(String status);

    /**
     * 根据房型查找客房
     */
    List<Room> findByRoomType(String roomType);

    /**
     * 根据状态和房型查找客房
     */
    List<Room> findByStatusAndRoomType(String status, String roomType);

    /**
     * 根据楼层查找客房
     */
    List<Room> findByFloor(Integer floor);

    /**
     * 检查房间号是否存在
     */
    boolean existsByRoomNumber(String roomNumber);
}
