package com.loopers.domain.member;

import java.util.Optional;

public interface MemberRepository {
    Optional<MemberModel> findByUserId(String userId);

    MemberModel create(MemberModel member);
}
