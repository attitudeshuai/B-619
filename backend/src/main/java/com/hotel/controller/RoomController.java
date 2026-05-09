package com.hotel.controller;

import com.hotel.dto.ApiResponse;
import com.hotel.dto.RoomRequest;
import com.hotel.entity.Room;
import com.hotel.service.RoomService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 客房控制器
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    @Autowired
    private RoomService roomService;

    /**
     * 分页查询客房
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Room>>> getRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String roomType) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("roomNumber").ascending());
        Page<Room> rooms = roomService.getRooms(pageable, status, roomType);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    /**
     * 获取客房详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Room>> getRoomById(@PathVariable Long id) {
        Room room = roomService.getRoomById(id);
        return ResponseEntity.ok(ApiResponse.success(room));
    }

    /**
     * 根据状态查询客房
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Room>>> getRoomsByStatus(@PathVariable String status) {
        List<Room> rooms = roomService.getRoomsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    /**
     * 新增客房
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Room>> createRoom(@Valid @RequestBody RoomRequest request) {
        logger.info("新增客房: {}", request.getRoomNumber());
        Room room = roomService.createRoom(request);
        return ResponseEntity.ok(ApiResponse.success("新增成功", room));
    }

    /**
     * 更新客房
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Room>> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        logger.info("更新客房: ID={}", id);
        Room room = roomService.updateRoom(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新成功", room));
    }

    /**
     * 删除客房
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id) {
        logger.info("删除客房: ID={}", id);
        roomService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    /**
     * 变更客房状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Room>> changeStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        logger.info("变更客房状态: ID={}, 状态={}", id, status);
        Room room = roomService.changeStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("状态已更新", room));
    }

    /**
     * 获取统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        Map<String, Object> stats = roomService.getStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
