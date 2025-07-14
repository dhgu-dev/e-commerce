package com.loopers.infrastructure.member;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<MemberModel> findByUserId(String userId) {
        return memberJpaRepository.findByUserId(userId);
    }

    @Override
    public MemberModel create(MemberModel member) {
        return memberJpaRepository.save(member);
    }
}
