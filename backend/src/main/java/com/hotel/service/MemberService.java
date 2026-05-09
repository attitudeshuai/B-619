package com.hotel.service;

import com.hotel.dto.MemberRequest;
import com.hotel.entity.Member;
import com.hotel.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 会员服务
 */
@Service
public class MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private MemberRepository memberRepository;

    public Page<Member> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("会员不存在"));
    }

    @Transactional
    public Member createMember(MemberRequest request) {
        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }

        if (request.getIdCard() != null && memberRepository.existsByIdCard(request.getIdCard())) {
            throw new RuntimeException("身份证号已存在");
        }

        Member member = new Member();
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setIdCard(request.getIdCard());
        member.setLevel("普通会员");
        member.setBalance(BigDecimal.ZERO);
        member.setPoints(0);

        Member saved = memberRepository.save(member);
        logger.info("新增会员: {}", saved.getName());
        return saved;
    }

    @Transactional
    public Member updateMember(Long id, MemberRequest request) {
        Member member = getMemberById(id);

        if (!member.getPhone().equals(request.getPhone())) {
            if (memberRepository.existsByPhone(request.getPhone())) {
                throw new RuntimeException("手机号已存在");
            }
            member.setPhone(request.getPhone());
        }

        if (request.getIdCard() != null && !request.getIdCard().equals(member.getIdCard())) {
            if (memberRepository.existsByIdCard(request.getIdCard())) {
                throw new RuntimeException("身份证号已存在");
            }
            member.setIdCard(request.getIdCard());
        }

        member.setName(request.getName());
        Member updated = memberRepository.save(member);
        logger.info("更新会员: {}", updated.getName());
        return updated;
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = getMemberById(id);
        memberRepository.delete(member);
        logger.info("删除会员: {}", member.getName());
    }

    @Transactional
    public Member recharge(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }

        Member member = getMemberById(id);
        member.setBalance(member.getBalance().add(amount));

        // 增加积分 (1元=1积分)
        member.setPoints(member.getPoints() + amount.intValue());

        // 自动升级会员等级
        upgradeLevel(member);

        Member updated = memberRepository.save(member);
        logger.info("会员 {} 充值 {} 元", member.getName(), amount);
        return updated;
    }

    @Transactional
    public Member adjustPoints(Long id, Integer points) {
        Member member = getMemberById(id);
        member.setPoints(Math.max(0, member.getPoints() + points));

        upgradeLevel(member);

        Member updated = memberRepository.save(member);
        logger.info("会员 {} 积分调整 {}", member.getName(), points);
        return updated;
    }

    private void upgradeLevel(Member member) {
        int points = member.getPoints();
        String newLevel;

        if (points >= 10000) {
            newLevel = "钻石卡";
        } else if (points >= 5000) {
            newLevel = "金卡";
        } else if (points >= 1000) {
            newLevel = "银卡";
        } else {
            newLevel = "普通会员";
        }

        if (!member.getLevel().equals(newLevel)) {
            logger.info("会员 {} 等级升级: {} -> {}", member.getName(), member.getLevel(), newLevel);
            member.setLevel(newLevel);
        }
    }
}
