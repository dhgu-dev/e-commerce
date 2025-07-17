package com.loopers.domain.member;

import com.loopers.domain.member.enums.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

@RequiredArgsConstructor
@Component
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberModel getMember(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CoreException(
                        ErrorType.NOT_FOUND,
                        MessageFormat.format("[userId = {0}] 회원을 찾을 수 없습니다.", userId)
                ));
    }

    @Transactional()
    public MemberModel createMember(String userId, Gender gender, String birthdate, String email) {
        return memberRepository.create(new MemberModel(userId, gender, birthdate, email));
    }

    @Transactional()
    public MemberModel chargePoints(String userId, Long points) {
        MemberModel member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CoreException(
                        ErrorType.NOT_FOUND,
                        MessageFormat.format("[userId = {0}] 회원을 찾을 수 없습니다.", userId)
                ));
        member.chargePoints(points);
        return memberRepository.update(member);
    }
}
