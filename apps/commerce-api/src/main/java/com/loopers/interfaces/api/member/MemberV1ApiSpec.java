package com.loopers.interfaces.api.member;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Member V1 API", description = "회원 API 입니다")
public interface MemberV1ApiSpec {

    @Operation(
            summary = "회원가입",
            description = "제공된 정보로 신규 회원을 생성합니다."
    )
    ApiResponse<MemberV1Dto.MemberResponse> signup(
            @RequestBody MemberV1Dto.SignupRequest dto
    );
}
