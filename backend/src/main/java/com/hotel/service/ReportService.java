package com.hotel.service;

import com.hotel.entity.CheckIn;
import com.hotel.entity.Member;
import com.hotel.entity.Payment;
import com.hotel.repository.CheckInRepository;
import com.hotel.repository.MemberRepository;
import com.hotel.repository.PaymentRepository;
import com.hotel.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表服务
 */
@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private MemberRepository memberRepository;

    public List<Map<String, Object>> getRevenueReport(LocalDate startDate, LocalDate endDate, String period) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Payment> payments = paymentRepository.findByPaymentTimeRange(start, end);
        Map<String, BigDecimal> aggregated = new HashMap<>();

        for (Payment payment : payments) {
            LocalDateTime time = payment.getPaymentTime();
            String key;
            if ("week".equalsIgnoreCase(period)) {
                int week = time.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                int weekYear = time.get(IsoFields.WEEK_BASED_YEAR);
                key = weekYear + "-W" + week;
            } else if ("month".equalsIgnoreCase(period)) {
                YearMonth ym = YearMonth.from(time);
                key = ym.toString();
            } else {
                key = time.toLocalDate().toString();
            }
            aggregated.put(key, aggregated.getOrDefault(key, BigDecimal.ZERO).add(payment.getAmount()));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        if ("week".equalsIgnoreCase(period)) {
            LocalDate cursor = startDate;
            while (!cursor.isAfter(endDate)) {
                int week = cursor.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                int weekYear = cursor.get(IsoFields.WEEK_BASED_YEAR);
                String key = weekYear + "-W" + week;
                result.add(buildRevenueItem(key, aggregated.getOrDefault(key, BigDecimal.ZERO)));
                cursor = cursor.plusWeeks(1);
            }
        } else if ("month".equalsIgnoreCase(period)) {
            YearMonth cursor = YearMonth.from(startDate);
            YearMonth endMonth = YearMonth.from(endDate);
            while (!cursor.isAfter(endMonth)) {
                String key = cursor.toString();
                result.add(buildRevenueItem(key, aggregated.getOrDefault(key, BigDecimal.ZERO)));
                cursor = cursor.plusMonths(1);
            }
        } else {
            LocalDate cursor = startDate;
            while (!cursor.isAfter(endDate)) {
                String key = cursor.toString();
                result.add(buildRevenueItem(key, aggregated.getOrDefault(key, BigDecimal.ZERO)));
                cursor = cursor.plusDays(1);
            }
        }

        logger.info("生成收入报表: {} - {}, 周期={}", startDate, endDate, period);
        return result;
    }

    private Map<String, Object> buildRevenueItem(String period, BigDecimal total) {
        Map<String, Object> item = new HashMap<>();
        item.put("period", period);
        item.put("total", total);
        return item;
    }

    public Map<String, Object> getOccupancyReport() {
        long total = roomRepository.count();
        long occupied = roomRepository.findByStatus("已入住").size();
        long booked = roomRepository.findByStatus("已预订").size();
        double occupancyRate = total > 0 ? (occupied * 100.0 / total) : 0;

        Map<String, Object> report = new HashMap<>();
        report.put("totalRooms", total);
        report.put("occupiedRooms", occupied);
        report.put("bookedRooms", booked);
        report.put("occupancyRate", occupancyRate);
        return report;
    }

    public List<Map<String, Object>> getMemberConsumptionReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Payment> payments = paymentRepository.findByPaymentTimeRange(start, end);
        Map<Long, BigDecimal> memberTotals = new HashMap<>();

        for (Payment payment : payments) {
            CheckIn checkIn = checkInRepository.findById(payment.getCheckInId()).orElse(null);
            if (checkIn == null) {
                continue;
            }
            Long memberId = checkIn.getMemberId();
            memberTotals.put(memberId, memberTotals.getOrDefault(memberId, BigDecimal.ZERO).add(payment.getAmount()));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : memberTotals.entrySet()) {
            Member member = memberRepository.findById(entry.getKey()).orElse(null);
            if (member == null) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("memberId", member.getId());
            item.put("memberName", member.getName());
            item.put("total", entry.getValue());
            result.add(item);
        }

        result.sort((m1, m2) -> {
            BigDecimal t1 = (BigDecimal) m1.get("total");
            BigDecimal t2 = (BigDecimal) m2.get("total");
            return t2.compareTo(t1);
        });
        return result;
    }
}
