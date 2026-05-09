package com.hotel.service;

import com.hotel.dto.BookingRequest;
import com.hotel.entity.Booking;
import com.hotel.entity.CheckIn;
import com.hotel.entity.Member;
import com.hotel.entity.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.CheckInRepository;
import com.hotel.repository.MemberRepository;
import com.hotel.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预订服务
 */
@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CheckInRepository checkInRepository;

    public Page<Booking> getBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预订不存在"));
    }

    @Transactional
    public Booking createBooking(BookingRequest request) {
        if (request.getCheckOutDate().isBefore(request.getCheckInDate())) {
            throw new RuntimeException("退房日期不能早于入住日期");
        }

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("客房不存在"));
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        if (!"空闲".equals(room.getStatus())) {
            throw new RuntimeException("客房当前不可预订");
        }

        Booking booking = new Booking();
        booking.setRoomId(room.getId());
        booking.setMemberId(member.getId());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setDeposit(request.getDeposit());
        booking.setRemark(request.getRemark());
        booking.setStatus("已预订");

        Booking saved = bookingRepository.save(booking);

        room.setStatus("已预订");
        roomRepository.save(room);

        logger.info("创建预订: ID={}, 房间={}, 会员={}", saved.getId(), room.getRoomNumber(), member.getName());
        return saved;
    }

    @Transactional
    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);

        if ("已取消".equals(booking.getStatus())) {
            throw new RuntimeException("预订已取消");
        }
        if ("已完成".equals(booking.getStatus())) {
            throw new RuntimeException("预订已完成，无法取消");
        }

        booking.setStatus("已取消");
        Booking updated = bookingRepository.save(booking);

        roomRepository.findById(booking.getRoomId()).ifPresent(room -> {
            if ("已预订".equals(room.getStatus())) {
                room.setStatus("空闲");
                roomRepository.save(room);
            }
        });

        logger.info("取消预订: ID={}", id);
        return updated;
    }

    @Transactional
    public CheckIn confirmCheckIn(Long id) {
        Booking booking = getBookingById(id);

        if (!"已预订".equals(booking.getStatus())) {
            throw new RuntimeException("当前预订状态无法办理入住");
        }

        Room room = roomRepository.findById(booking.getRoomId())
                .orElseThrow(() -> new RuntimeException("客房不存在"));
        if (!"已预订".equals(room.getStatus()) && !"空闲".equals(room.getStatus())) {
            throw new RuntimeException("客房状态不可入住");
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setRoomId(booking.getRoomId());
        checkIn.setMemberId(booking.getMemberId());
        checkIn.setBookingId(booking.getId());
        checkIn.setStatus("已入住");
        checkIn.setCheckInTime(LocalDateTime.now());

        CheckIn savedCheckIn = checkInRepository.save(checkIn);

        booking.setStatus("已确认");
        bookingRepository.save(booking);

        room.setStatus("已入住");
        roomRepository.save(room);

        logger.info("预订确认入住: 预订ID={}, 入住ID={}", booking.getId(), savedCheckIn.getId());
        return savedCheckIn;
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus("空闲");
    }
}
