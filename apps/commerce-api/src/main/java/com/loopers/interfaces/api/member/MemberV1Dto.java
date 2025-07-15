package com.loopers.interfaces.api.member;

import com.loopers.application.member.MemberInfo;
import com.loopers.domain.member.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public class MemberV1Dto {
    public record MemberResponse(Long id, String userId, Gender gender, LocalDate birthdate, String email, Long points) {
        public static MemberResponse from(MemberInfo member) {
            return new MemberResponse(
                    member.id(),
                    member.userId(),
                    member.gender(),
                    member.birthdate(),
                    member.email(),
                    member.points()
            );
        }
    }

    public record SignupRequest(
            @Schema(name = "회원 ID", example = "test")
            String userId,
            @Schema(name = "성별", example = "MALE", allowableValues = {"FEMALE", "MALE"})
            Gender gender,
            @Schema(name = "생년월일", example = "2000-01-01")
            String birthdate,
            @Schema(name = "이메일", example = "test@test.com")
            String email
    ) {
    }
}
