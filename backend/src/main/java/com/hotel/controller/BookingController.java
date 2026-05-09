package com.hotel.controller;

import com.hotel.dto.ApiResponse;
import com.hotel.dto.BookingRequest;
import com.hotel.entity.Booking;
import com.hotel.entity.CheckIn;
import com.hotel.entity.Room;
import com.hotel.service.BookingService;
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

/**
 * 预订控制器
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Booking>>> getBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Booking> bookings = bookingService.getBookings(pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Booking>> createBooking(@Valid @RequestBody BookingRequest request) {
        logger.info("创建预订: 房间={}, 会员={}", request.getRoomId(), request.getMemberId());
        Booking booking = bookingService.createBooking(request);
        return ResponseEntity.ok(ApiResponse.success("预订成功", booking));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Booking>> cancelBooking(@PathVariable Long id) {
        Booking booking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(ApiResponse.success("预订已取消", booking));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<CheckIn>> confirmCheckIn(@PathVariable Long id) {
        CheckIn checkIn = bookingService.confirmCheckIn(id);
        return ResponseEntity.ok(ApiResponse.success("入住办理成功", checkIn));
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<ApiResponse<List<Room>>> getAvailableRooms() {
        List<Room> rooms = bookingService.getAvailableRooms();
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }
}
