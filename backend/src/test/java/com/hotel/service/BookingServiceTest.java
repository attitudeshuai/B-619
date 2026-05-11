package com.hotel.service;

import com.hotel.dto.BookingRequest;
import com.hotel.entity.Booking;
import com.hotel.entity.Member;
import com.hotel.entity.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.MemberRepository;
import com.hotel.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void should_create_booking_successfully_when_room_is_available() {
        LocalDate checkInDate = LocalDate.now().plusDays(1);
        LocalDate checkOutDate = LocalDate.now().plusDays(3);

        BookingRequest request = new BookingRequest();
        request.setRoomId(1L);
        request.setMemberId(1L);
        request.setCheckInDate(checkInDate);
        request.setCheckOutDate(checkOutDate);
        request.setDeposit(new BigDecimal("100.00"));
        request.setRemark("测试预订");

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setStatus("空闲");

        Member member = new Member();
        member.setId(1L);
        member.setName("测试会员");

        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setRoomId(1L);
        savedBooking.setMemberId(1L);
        savedBooking.setCheckInDate(checkInDate);
        savedBooking.setCheckOutDate(checkOutDate);
        savedBooking.setStatus("已预订");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        Booking result = bookingService.createBooking(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("已预订", result.getStatus());
        verify(roomRepository).save(room);
        assertEquals("已预订", room.getStatus());
    }

    @Test
    void should_throw_exception_when_room_is_already_booked() {
        LocalDate checkInDate = LocalDate.now().plusDays(1);
        LocalDate checkOutDate = LocalDate.now().plusDays(3);

        BookingRequest request = new BookingRequest();
        request.setRoomId(1L);
        request.setMemberId(1L);
        request.setCheckInDate(checkInDate);
        request.setCheckOutDate(checkOutDate);

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setStatus("已预订");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("客房当前不可预订", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void should_throw_exception_when_check_out_date_is_before_check_in_date() {
        LocalDate checkInDate = LocalDate.now().plusDays(3);
        LocalDate checkOutDate = LocalDate.now().plusDays(1);

        BookingRequest request = new BookingRequest();
        request.setRoomId(1L);
        request.setMemberId(1L);
        request.setCheckInDate(checkInDate);
        request.setCheckOutDate(checkOutDate);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("退房日期不能早于入住日期", exception.getMessage());
        verify(roomRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void should_cancel_booking_successfully_when_booking_is_valid() {
        Long bookingId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setRoomId(1L);
        booking.setStatus("已预订");

        Room room = new Room();
        room.setId(1L);
        room.setStatus("已预订");

        Booking cancelledBooking = new Booking();
        cancelledBooking.setId(bookingId);
        cancelledBooking.setStatus("已取消");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.save(any(Booking.class))).thenReturn(cancelledBooking);

        Booking result = bookingService.cancelBooking(bookingId);

        assertNotNull(result);
        assertEquals("已取消", result.getStatus());
        verify(roomRepository).save(room);
        assertEquals("空闲", room.getStatus());
    }

    @Test
    void should_throw_exception_when_cancelling_already_cancelled_booking() {
        Long bookingId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus("已取消");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.cancelBooking(bookingId);
        });

        assertEquals("预订已取消", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void should_throw_exception_when_cancelling_completed_booking() {
        Long bookingId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus("已完成");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.cancelBooking(bookingId);
        });

        assertEquals("预订已完成，无法取消", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}
