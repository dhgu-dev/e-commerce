package com.loopers.interfaces.api.member;

import com.loopers.application.member.MemberFacade;
import com.loopers.application.member.MemberInfo;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ApiResponse.success(MemberV1Dto.MemberResponse.from(memberFacade.getMember(userId)));
    }
}
