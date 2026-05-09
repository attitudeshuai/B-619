package com.hotel.service;

import com.hotel.dto.RoomRequest;
import com.hotel.entity.Room;
import com.hotel.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客房服务
 */
@Service
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    private RoomRepository roomRepository;

    /**
     * 分页查询客房
     */
    public Page<Room> getRooms(Pageable pageable, String status, String roomType) {
        if (status != null && !status.isBlank() && roomType != null && !roomType.isBlank()) {
            List<Room> rooms = roomRepository.findByStatusAndRoomType(status, roomType);
            return toPage(rooms, pageable);
        }
        if (status != null && !status.isBlank()) {
            List<Room> rooms = roomRepository.findByStatus(status);
            return toPage(rooms, pageable);
        }
        if (roomType != null && !roomType.isBlank()) {
            List<Room> rooms = roomRepository.findByRoomType(roomType);
            return toPage(rooms, pageable);
        }
        return roomRepository.findAll(pageable);
    }

    private Page<Room> toPage(List<Room> rooms, Pageable pageable) {
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), rooms.size());
        if (start > rooms.size()) {
            start = rooms.size();
        }
        List<Room> subList = rooms.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(subList, pageable, rooms.size());
    }

    /**
     * 根据状态查询客房
     */
    public List<Room> getRoomsByStatus(String status) {
        return roomRepository.findByStatus(status);
    }

    /**
     * 根据房型查询客房
     */
    public List<Room> getRoomsByType(String roomType) {
        return roomRepository.findByRoomType(roomType);
    }

    /**
     * 根据 ID 查询客房
     */
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("客房不存在"));
    }

    /**
     * 新增客房
     */
    @Transactional
    public Room createRoom(RoomRequest request) {
        // 检查房间号是否已存在
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new RuntimeException("房间号已存在");
        }

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setPrice(request.getPrice());
        room.setFloor(request.getFloor());
        room.setDescription(request.getDescription());
        room.setStatus("空闲");

        Room saved = roomRepository.save(room);
        logger.info("新增客房: {}", saved.getRoomNumber());
        return saved;
    }

    /**
     * 更新客房
     */
    @Transactional
    public Room updateRoom(Long id, RoomRequest request) {
        Room room = getRoomById(id);

        // 如果修改了房间号，检查新房间号是否已存在
        if (!room.getRoomNumber().equals(request.getRoomNumber())) {
            if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
                throw new RuntimeException("房间号已存在");
            }
            room.setRoomNumber(request.getRoomNumber());
        }

        room.setRoomType(request.getRoomType());
        room.setPrice(request.getPrice());
        room.setFloor(request.getFloor());
        room.setDescription(request.getDescription());

        Room updated = roomRepository.save(room);
        logger.info("更新客房: {}", updated.getRoomNumber());
        return updated;
    }

    /**
     * 删除客房
     */
    @Transactional
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);

        // 只能删除停用状态的客房
        if (!"停用".equals(room.getStatus())) {
            throw new RuntimeException("只能删除停用状态的客房");
        }

        roomRepository.delete(room);
        logger.info("删除客房: {}", room.getRoomNumber());
    }

    /**
     * 变更客房状态
     */
    @Transactional
    public Room changeStatus(Long id, String status) {
        Room room = getRoomById(id);

        // 验证状态
        if (!List.of("空闲", "已预订", "已入住", "维护中", "停用").contains(status)) {
            throw new RuntimeException("无效的状态");
        }

        room.setStatus(status);
        Room updated = roomRepository.save(room);
        logger.info("客房 {} 状态变更为: {}", room.getRoomNumber(), status);
        return updated;
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long total = roomRepository.count();
        long available = roomRepository.findByStatus("空闲").size();
        long booked = roomRepository.findByStatus("已预订").size();
        long occupied = roomRepository.findByStatus("已入住").size();
        long maintenance = roomRepository.findByStatus("维护中").size();

        stats.put("total", total);
        stats.put("available", available);
        stats.put("booked", booked);
        stats.put("occupied", occupied);
        stats.put("maintenance", maintenance);
        stats.put("occupancyRate", total > 0 ? (occupied * 100.0 / total) : 0);

        return stats;
    }
}
