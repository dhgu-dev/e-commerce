package com.loopers.application.member;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.enums.Gender;

import java.time.LocalDate;

public record MemberInfo(Long id, String userId, Gender gender, LocalDate birthdate, String email, Long points) {
    public static MemberInfo from(MemberModel model) {
        return new MemberInfo(
                model.getId(),
                model.getUserId(),
                model.getGender(),
                model.getBirthdate(),
                model.getEmail(),
                model.getPoints()
        );
    }
}
