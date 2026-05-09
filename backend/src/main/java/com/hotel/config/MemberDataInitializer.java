package com.hotel.config;

import com.hotel.entity.Member;
import com.hotel.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 会员数据初始化
 */
@Component
@Order(3)
public class MemberDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MemberDataInitializer.class);

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void run(String... args) {
        if (memberRepository.count() == 0) {
            logger.info("初始化会员数据...");

            List<Member> members = new ArrayList<>();

            Member member1 = new Member();
            member1.setName("李明");
            member1.setPhone("13800000001");
            member1.setIdCard("110101199001010011");
            member1.setLevel("银卡");
            member1.setBalance(new BigDecimal("200.00"));
            member1.setPoints(1500);
            members.add(member1);

            Member member2 = new Member();
            member2.setName("王芳");
            member2.setPhone("13800000002");
            member2.setIdCard("110101199002020022");
            member2.setLevel("金卡");
            member2.setBalance(new BigDecimal("500.00"));
            member2.setPoints(5200);
            members.add(member2);

            Member member3 = new Member();
            member3.setName("张伟");
            member3.setPhone("13800000003");
            member3.setIdCard("110101199003030033");
            member3.setLevel("普通会员");
            member3.setBalance(new BigDecimal("80.00"));
            member3.setPoints(200);
            members.add(member3);

            Member member4 = new Member();
            member4.setName("陈洁");
            member4.setPhone("13800000004");
            member4.setIdCard("110101199004040044");
            member4.setLevel("钻石卡");
            member4.setBalance(new BigDecimal("1200.00"));
            member4.setPoints(12000);
            members.add(member4);

            Member member5 = new Member();
            member5.setName("刘洋");
            member5.setPhone("13800000005");
            member5.setIdCard("110101199005050055");
            member5.setLevel("普通会员");
            member5.setBalance(new BigDecimal("50.00"));
            member5.setPoints(100);
            members.add(member5);

            memberRepository.saveAll(members);
            logger.info("会员数据初始化完成，共创建 {} 位会员", members.size());
        } else {
            logger.info("会员数据已存在，跳过初始化");
        }
    }
}
