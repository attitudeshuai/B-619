package com.hotel.service;

import com.hotel.dto.CheckInRequest;
import com.hotel.dto.CheckOutRequest;
import com.hotel.dto.ExtendRequest;
import com.hotel.entity.Booking;
import com.hotel.entity.CheckIn;
import com.hotel.entity.Payment;
import com.hotel.entity.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.CheckInRepository;
import com.hotel.repository.PaymentRepository;
import com.hotel.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 入住登记服务
 */
@Service
public class CheckInService {

    private static final Logger logger = LoggerFactory.getLogger(CheckInService.class);

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MemberService memberService;

    public Page<CheckIn> getCheckIns(Pageable pageable) {
        return checkInRepository.findAll(pageable);
    }

    public CheckIn getCheckInById(Long id) {
        return checkInRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("入住记录不存在"));
    }

    @Transactional
    public CheckIn checkIn(CheckInRequest request) {
        memberService.getMemberById(request.getMemberId());
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("客房不存在"));

        if (!"空闲".equals(room.getStatus()) && !"已预订".equals(room.getStatus())) {
            throw new RuntimeException("客房状态不可入住");
        }

        if (request.getBookingId() != null) {
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new RuntimeException("预订不存在"));

            if (!"已预订".equals(booking.getStatus())) {
                throw new RuntimeException("预订状态不可入住");
            }
            if (!booking.getMemberId().equals(request.getMemberId())) {
                throw new RuntimeException("预订会员信息不匹配");
            }
            if (!booking.getRoomId().equals(request.getRoomId())) {
                throw new RuntimeException("预订客房信息不匹配");
            }

            booking.setStatus("已确认");
            bookingRepository.save(booking);
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setRoomId(request.getRoomId());
        checkIn.setMemberId(request.getMemberId());
        checkIn.setBookingId(request.getBookingId());
        checkIn.setCheckInTime(LocalDateTime.now());
        checkIn.setStatus("已入住");
        checkIn.setRemark(request.getRemark());

        CheckIn saved = checkInRepository.save(checkIn);

        room.setStatus("已入住");
        roomRepository.save(room);

        logger.info("办理入住: 房间={}, 入住ID={}", room.getRoomNumber(), saved.getId());
        return saved;
    }

    @Transactional
    public CheckIn checkOut(Long id, CheckOutRequest request) {
        CheckIn checkIn = getCheckInById(id);

        if ("已退房".equals(checkIn.getStatus())) {
            throw new RuntimeException("已退房记录无法重复结算");
        }

        checkIn.setStatus("已退房");
        checkIn.setCheckOutTime(LocalDateTime.now());

        CheckIn updated = checkInRepository.save(checkIn);

        roomRepository.findById(checkIn.getRoomId()).ifPresent(room -> {
            room.setStatus("空闲");
            roomRepository.save(room);
        });

        Payment payment = new Payment();
        payment.setCheckInId(checkIn.getId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentTime(LocalDateTime.now());
        payment.setRemark(request.getRemark());
        paymentRepository.save(payment);

        if (checkIn.getBookingId() != null) {
            bookingRepository.findById(checkIn.getBookingId()).ifPresent(booking -> {
                booking.setStatus("已完成");
                bookingRepository.save(booking);
            });
        }

        if (request.getAmount().intValue() > 0) {
            memberService.adjustPoints(checkIn.getMemberId(), request.getAmount().intValue());
        }

        logger.info("办理退房: 入住ID={}, 结算金额={}", checkIn.getId(), request.getAmount());
        return updated;
    }

    @Transactional
    public CheckIn extendStay(Long id, ExtendRequest request) {
        CheckIn checkIn = getCheckInById(id);

        if (!"已入住".equals(checkIn.getStatus())) {
            throw new RuntimeException("非入住状态无法续住");
        }

        LocalDateTime baseTime = checkIn.getCheckOutTime() != null
                ? checkIn.getCheckOutTime()
                : LocalDateTime.now();
        checkIn.setCheckOutTime(baseTime.plusDays(request.getExtendDays()));

        CheckIn updated = checkInRepository.save(checkIn);
        logger.info("办理续住: 入住ID={}, 续住天数={}", checkIn.getId(), request.getExtendDays());
        return updated;
    }
}
