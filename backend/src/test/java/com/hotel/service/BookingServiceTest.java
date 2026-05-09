package com.hotel.service;

import com.hotel.dto.BookingRequest;
import com.hotel.entity.Booking;
import com.hotel.entity.Member;
import com.hotel.entity.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.CheckInRepository;
import com.hotel.repository.MemberRepository;
import com.hotel.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    private Room room;
    private Member member;
    private BookingRequest request;
    private Booking booking;

    @BeforeEach
    void setUp() {
        room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setRoomType("标准间");
        room.setPrice(new BigDecimal("299.00"));
        room.setFloor(1);
        room.setStatus("空闲");

        member = new Member();
        member.setId(1L);
        member.setName("张三");
        member.setPhone("13800138000");
        member.setLevel("普通会员");
        member.setBalance(BigDecimal.ZERO);
        member.setPoints(0);

        request = new BookingRequest();
        request.setRoomId(1L);
        request.setMemberId(1L);
        request.setCheckInDate(LocalDate.of(2026, 6, 1));
        request.setCheckOutDate(LocalDate.of(2026, 6, 3));
        request.setDeposit(new BigDecimal("300.00"));
        request.setRemark("测试预订");

        booking = new Booking();
        booking.setId(1L);
        booking.setRoomId(1L);
        booking.setMemberId(1L);
        booking.setCheckInDate(LocalDate.of(2026, 6, 1));
        booking.setCheckOutDate(LocalDate.of(2026, 6, 3));
        booking.setDeposit(new BigDecimal("300.00"));
        booking.setRemark("测试预订");
        booking.setStatus("已预订");
    }

    @Test
    @DisplayName("should_createBookingSuccessfully_when_roomIsAvailable")
    void should_createBookingSuccessfully_when_roomIsAvailable() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Booking result = bookingService.createBooking(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRoomId()).isEqualTo(1L);
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getCheckInDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        assertThat(result.getCheckOutDate()).isEqualTo(LocalDate.of(2026, 6, 3));
        assertThat(result.getStatus()).isEqualTo("已预订");
        assertThat(result.getDeposit()).isEqualByComparingTo(new BigDecimal("300.00"));

        verify(bookingRepository).save(any(Booking.class));
        assertThat(room.getStatus()).isEqualTo("已预订");
        verify(roomRepository).save(room);
    }

    @Test
    @DisplayName("should_throwConflictError_when_roomIsAlreadyBooked")
    void should_throwConflictError_when_roomIsAlreadyBooked() {
        room.setStatus("已预订");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("客房当前不可预订");

        verify(bookingRepository, never()).save(any(Booking.class));
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("should_throwValidationError_when_checkInDateIsAfterCheckOutDate")
    void should_throwValidationError_when_checkInDateIsAfterCheckOutDate() {
        request.setCheckInDate(LocalDate.of(2026, 6, 5));
        request.setCheckOutDate(LocalDate.of(2026, 6, 3));

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("退房日期不能早于入住日期");

        verify(roomRepository, never()).findById(any());
        verify(memberRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("should_cancelBookingSuccessfully_when_bookingIsActive")
    void should_cancelBookingSuccessfully_when_bookingIsActive() {
        booking.setStatus("已预订");
        room.setStatus("已预订");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Booking result = bookingService.cancelBooking(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("已取消");

        verify(bookingRepository).save(any(Booking.class));

        assertThat(room.getStatus()).isEqualTo("空闲");
        verify(roomRepository).save(room);
    }

    @Test
    @DisplayName("should_throwError_when_cancellingAlreadyCancelledBooking")
    void should_throwError_when_cancellingAlreadyCancelledBooking() {
        booking.setStatus("已取消");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("预订已取消");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("should_throwError_when_cancellingCompletedBooking")
    void should_throwError_when_cancellingCompletedBooking() {
        booking.setStatus("已完成");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("预订已完成，无法取消");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("should_notChangeRoomStatus_when_cancellingBookingAndRoomNotBooked")
    void should_notChangeRoomStatus_when_cancellingBookingAndRoomNotBooked() {
        booking.setStatus("已预订");
        room.setStatus("已入住");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        Booking result = bookingService.cancelBooking(1L);

        assertThat(result.getStatus()).isEqualTo("已取消");
        assertThat(room.getStatus()).isEqualTo("已入住");
        verify(roomRepository, never()).save(any(Room.class));
    }
}
