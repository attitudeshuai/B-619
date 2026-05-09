package com.hotel.repository;

import com.hotel.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 会员数据访问层
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPhone(String phone);

    Optional<Member> findByIdCard(String idCard);

    List<Member> findByLevel(String level);

    boolean existsByPhone(String phone);

    boolean existsByIdCard(String idCard);
}
