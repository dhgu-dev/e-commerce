package com.loopers.application.points;

import com.loopers.domain.member.MemberService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointsFacade {
    private final MemberService memberService;

    public Long chargePoints(String userId, Long points) {
        if (points <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트는 0보다 커야 합니다.");
        }
        return memberService.chargePoints(userId, points).getPoints();
    }
}
