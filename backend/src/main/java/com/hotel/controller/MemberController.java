package com.hotel.controller;

import com.hotel.dto.ApiResponse;
import com.hotel.dto.MemberRequest;
import com.hotel.entity.Member;
import com.hotel.service.MemberService;
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

import java.math.BigDecimal;
import java.util.Map;

/**
 * 会员控制器
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Member>>> getMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Member> members = memberService.getMembers(pageable);

        return ResponseEntity.ok(ApiResponse.success(members));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> getMemberById(@PathVariable Long id) {
        Member member = memberService.getMemberById(id);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Member>> createMember(@Valid @RequestBody MemberRequest request) {
        logger.info("新增会员: {}", request.getName());
        Member member = memberService.createMember(request);
        return ResponseEntity.ok(ApiResponse.success("新增成功", member));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {
        logger.info("更新会员: ID={}", id);
        Member member = memberService.updateMember(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新成功", member));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id) {
        logger.info("删除会员: ID={}", id);
        memberService.deleteMember(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @PostMapping("/{id}/recharge")
    public ResponseEntity<ApiResponse<Member>> recharge(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        logger.info("会员充值: ID={}, 金额={}", id, amount);
        Member member = memberService.recharge(id, amount);
        return ResponseEntity.ok(ApiResponse.success("充值成功", member));
    }

    @PostMapping("/{id}/points")
    public ResponseEntity<ApiResponse<Member>> adjustPoints(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer points = request.get("points");
        logger.info("积分调整: ID={}, 积分={}", id, points);
        Member member = memberService.adjustPoints(id, points);
        return ResponseEntity.ok(ApiResponse.success("积分已调整", member));
    }
}
