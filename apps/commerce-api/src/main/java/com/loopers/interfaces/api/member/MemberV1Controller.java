package com.loopers.interfaces.api.member;

import com.loopers.application.member.MemberFacade;
import com.loopers.application.member.MemberInfo;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class MemberV1Controller implements MemberV1ApiSpec {

    private final MemberFacade memberFacade;

    @PostMapping()
    @Override
    public ApiResponse<MemberV1Dto.MemberResponse> signup(MemberV1Dto.SignupRequest dto) {
        MemberInfo member = memberFacade.signUp(
                dto.userId(),
                dto.gender(),
                dto.birthdate(),
                dto.email()
        );
        return ApiResponse.success(MemberV1Dto.MemberResponse.from(member));
    }

    @GetMapping("/me")
    @Override
    public ApiResponse<MemberV1Dto.MemberResponse> getMyInfo(String userId) {
        MemberInfo member = memberFacade.getMember(userId);
        if (member == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MessageFormat.format("[userId = {0}] 회원을 찾을 수 없습니다.", userId));
        }
        return ApiResponse.success(MemberV1Dto.MemberResponse.from(member));
    }
}
