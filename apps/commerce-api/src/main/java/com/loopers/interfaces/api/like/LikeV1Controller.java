package com.loopers.interfaces.api.like;

import com.loopers.application.like.usecase.command.CommandMarkLikeUseCase;
import com.loopers.application.like.usecase.command.CommandUnmarkLikeUseCase;
import com.loopers.application.like.usecase.query.QueryMemberLikedProductsUseCase;
import com.loopers.application.member.MemberFacade;
import com.loopers.application.member.MemberInfo;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.like.LikeV1Dto.GetLikedProductsResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeV1Controller implements LikeV1ApiSpec {

    private final CommandMarkLikeUseCase commandMarkLikeUseCase;
    private final CommandUnmarkLikeUseCase commandUnmarkLikeUseCase;
    private final QueryMemberLikedProductsUseCase queryMemberLikedProductsUseCase;
    private final MemberFacade memberFacade;

    @Override
    @PostMapping("/products/{productId}")
    public ApiResponse<LikeV1Dto.LikeResponse> likeProduct(
        @RequestHeader(value = "X-USER-ID", required = false) String userId,
        @PathVariable("productId") Long productId
    ) {
        if (userId == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }
        MemberInfo memberInfo = memberFacade.getMember(userId);
        if (memberInfo == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }

        commandMarkLikeUseCase.execute(new CommandMarkLikeUseCase.Command(memberInfo, productId));
        return ApiResponse.success(LikeV1Dto.LikeResponse.from(userId, productId, true));
    }

    @Override
    @DeleteMapping("/products/{productId}")
    public ApiResponse<LikeV1Dto.LikeResponse> unlikeProduct(
        @RequestHeader(value = "X-USER-ID", required = false) String userId,
        @PathVariable("productId") Long productId
    ) {
        if (userId == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }
        MemberInfo memberInfo = memberFacade.getMember(userId);
        if (memberInfo == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }

        commandUnmarkLikeUseCase.execute(new CommandUnmarkLikeUseCase.Command(memberInfo, productId));
        return ApiResponse.success(LikeV1Dto.LikeResponse.from(userId, productId, false));
    }

    @Override
    @GetMapping("/products")
    public ApiResponse<GetLikedProductsResponse> getLikedProducts(
        @RequestHeader(value = "X-USER-ID", required = false) String userId,
        @ModelAttribute LikeV1Dto.GetLikedProductsRequest dto
    ) {
        if (userId == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }
        MemberInfo memberInfo = memberFacade.getMember(userId);
        if (memberInfo == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }
        PageRequest pageRequest = PageRequest.of(dto.page(), dto.size());

        QueryMemberLikedProductsUseCase.Result result = queryMemberLikedProductsUseCase.execute(new QueryMemberLikedProductsUseCase.Query(memberInfo, pageRequest));

        return ApiResponse.success(
            GetLikedProductsResponse.from(
                GetLikedProductsResponse.MemberResponse.from(result.member()),
                result.products().map(GetLikedProductsResponse.ProductResponse::from)
            )
        );
    }
}
