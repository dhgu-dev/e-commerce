package com.loopers.application.member;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberService;
import com.loopers.domain.member.enums.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberFacade {
    private final MemberService memberService;

    public MemberInfo signUp(String userId, Gender gender, String birthdate, String email) {
        try {
            MemberModel member = memberService.getMember(userId);
            if (member != null) {
                throw new CoreException(ErrorType.CONFLICT, "이미 존재하는 회원 아이디 입니다.");
            }
        } catch (CoreException e) {
            if (e.getErrorType() == ErrorType.CONFLICT) {
                throw e;
            }
        }

        return MemberInfo.from(memberService.createMember(userId, gender, birthdate, email));
    }
}
