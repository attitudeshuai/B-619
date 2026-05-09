package com.hotel.controller;

import com.hotel.dto.ApiResponse;
import com.hotel.dto.CheckInRequest;
import com.hotel.dto.CheckOutRequest;
import com.hotel.dto.ExtendRequest;
import com.hotel.entity.CheckIn;
import com.hotel.service.CheckInService;
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

/**
 * 入住登记控制器
 */
@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    private static final Logger logger = LoggerFactory.getLogger(CheckInController.class);

    @Autowired
    private CheckInService checkInService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CheckIn>>> getCheckIns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("checkInTime").descending());
        Page<CheckIn> checkIns = checkInService.getCheckIns(pageable);
        return ResponseEntity.ok(ApiResponse.success(checkIns));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CheckIn>> checkIn(@Valid @RequestBody CheckInRequest request) {
        logger.info("办理入住: 房间={}, 会员={}", request.getRoomId(), request.getMemberId());
        CheckIn checkIn = checkInService.checkIn(request);
        return ResponseEntity.ok(ApiResponse.success("入住成功", checkIn));
    }

    @PutMapping("/{id}/checkout")
    public ResponseEntity<ApiResponse<CheckIn>> checkOut(
            @PathVariable Long id,
            @Valid @RequestBody CheckOutRequest request) {
        CheckIn checkIn = checkInService.checkOut(id, request);
        return ResponseEntity.ok(ApiResponse.success("退房成功", checkIn));
    }

    @PutMapping("/{id}/extend")
    public ResponseEntity<ApiResponse<CheckIn>> extend(
            @PathVariable Long id,
            @Valid @RequestBody ExtendRequest request) {
        CheckIn checkIn = checkInService.extendStay(id, request);
        return ResponseEntity.ok(ApiResponse.success("续住成功", checkIn));
    }
}
