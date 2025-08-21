package com.loopers.infrastructure.member;

import com.loopers.domain.member.MemberModel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<MemberModel, Long> {
    Optional<MemberModel> findByUserId(String userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MemberModel m where m.id = :id")
    Optional<MemberModel> findWithLock(@Param("id") Long id);

    @Modifying
    @Query("update MemberModel m set m.points = :points where m.id = :id")
    void updateMemberPoints(@Param("id") Long id, @Param("points") Long points);
}
