package com.loopers.domain.member;

import com.loopers.domain.member.enums.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Transactional()
    public void payment(MemberModel member, BigDecimal totalPrice) {
        if (member == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Member cannot be null.");
        }
        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Total price must be greater than zero.");
        }

        if (member.getPoints() < totalPrice.longValue()) {
            throw new CoreException(ErrorType.CONFLICT, "Insufficient points for payment.");
        }

        member.usePoints(totalPrice.longValue());
        memberRepository.update(member);
    }
}
