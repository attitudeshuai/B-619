package com.hotel.service;

import com.hotel.dto.BookingRequest;
import com.hotel.entity.Booking;
import com.hotel.entity.Member;
import com.hotel.entity.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.CheckInRepository;
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

    @Mock
    private CheckInRepository checkInRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void should_create_booking_successfully_when_request_is_valid() {
        BookingRequest request = new BookingRequest();
        request.setRoomId(1L);
        request.setMemberId(1L);
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(3));
        request.setDeposit(new BigDecimal("200.00"));
        request.setRemark("测试预订");

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setStatus("空闲");

        Member member = new Member();
        member.setId(1L);
        member.setName("张三");

        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setRoomId(1L);
        savedBooking.setMemberId(1L);
        savedBooking.setCheckInDate(request.getCheckInDate());
        savedBooking.setCheckOutDate(request.getCheckOutDate());
        savedBooking.setStatus("已预订");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        Booking result = bookingService.createBooking(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("已预订", result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(roomRepository, times(1)).save(room);
        assertEquals("已预订", room.getStatus());
    }

    @Test
    void should_throw_exception_when_room_is_not_available() {
        BookingRequest request = new BookingRequest();
        request.setRoomId(1L);
        request.setMemberId(1L);
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(3));

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setStatus("已预订");

        Member member = new Member();
        member.setId(1L);
        member.setName("张三");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("客房当前不可预订", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void should_throw_exception_when_check_out_date_is_before_check_in_date() {
        BookingRequest request = new BookingRequest();
        request.setRoomId(1L);
        request.setMemberId(1L);
        request.setCheckInDate(LocalDate.now().plusDays(3));
        request.setCheckOutDate(LocalDate.now().plusDays(1));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("退房日期不能早于入住日期", exception.getMessage());
        verify(roomRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void should_cancel_booking_successfully_when_booking_exists_and_not_cancelled() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setRoomId(1L);
        booking.setMemberId(1L);
        booking.setStatus("已预订");

        Room room = new Room();
        room.setId(1L);
        room.setStatus("已预订");

        Booking updatedBooking = new Booking();
        updatedBooking.setId(1L);
        updatedBooking.setRoomId(1L);
        updatedBooking.setMemberId(1L);
        updatedBooking.setStatus("已取消");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);

        Booking result = bookingService.cancelBooking(1L);

        assertNotNull(result);
        assertEquals("已取消", result.getStatus());
        verify(bookingRepository, times(1)).save(booking);
        verify(roomRepository, times(1)).save(room);
        assertEquals("空闲", room.getStatus());
    }

    @Test
    void should_throw_exception_when_cancelling_already_cancelled_booking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus("已取消");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.cancelBooking(1L);
        });

        assertEquals("预订已取消", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void should_throw_exception_when_cancelling_completed_booking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus("已完成");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.cancelBooking(1L);
        });

        assertEquals("预订已完成，无法取消", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(roomRepository, never()).save(any(Room.class));
    }
}
