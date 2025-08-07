package com.loopers.interfaces.api.orders;

import com.loopers.application.member.MemberFacade;
import com.loopers.application.member.MemberInfo;
import com.loopers.application.orders.usecase.command.CommandOrderUseCase;
import com.loopers.application.orders.usecase.query.QueryMemberOrdersUseCase;
import com.loopers.application.orders.usecase.query.QueryOrderDetailUseCase;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderV1Controller implements OrderV1ApiSpec {

    private final MemberFacade memberFacade;
    private final CommandOrderUseCase commandOrderUseCase;
    private final QueryMemberOrdersUseCase queryMemberOrdersUseCase;
    private final QueryOrderDetailUseCase queryOrderDetailUseCase;

    @Override
    @PostMapping()
    public ApiResponse<OrderV1Dto.OrderResponse> order(
        @RequestHeader(value = "X-USER-ID", required = false) String userId,
        @RequestBody OrderV1Dto.OrderRequest dto
    ) {
        if (userId == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }

        MemberInfo memberInfo = memberFacade.getMember(userId);

        if (memberInfo == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }

        List<Long> productIds = dto.items().stream()
            .map(OrderV1Dto.OrderRequest.OrderItemRequest::productId)
            .toList();
        List<Long> quantities = dto.items().stream()
            .map(OrderV1Dto.OrderRequest.OrderItemRequest::quantity)
            .toList();

        var result = commandOrderUseCase.execute(
            new CommandOrderUseCase.Command(
                memberInfo,
                productIds,
                quantities,
                dto.couponId()
            )
        );

        return ApiResponse.success(OrderV1Dto.OrderResponse.from(result.orderInfo()));
    }

    @Override
    @GetMapping()
    public ApiResponse<List<OrderV1Dto.OrderResponse>> getOrders(
        @RequestHeader(value = "X-USER-ID", required = false) String userId
    ) {
        if (userId == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }

        MemberInfo memberInfo = memberFacade.getMember(userId);

        if (memberInfo == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }

        var result = queryMemberOrdersUseCase.execute(
            new QueryMemberOrdersUseCase.Query(memberInfo)
        );

        return ApiResponse.success(
            result.orders().stream().map(OrderV1Dto.OrderResponse::from).toList()
        );
    }

    @Override
    @GetMapping("/{orderId}")
    public ApiResponse<OrderV1Dto.OrderResponse> getOrder(
        @RequestHeader(value = "X-USER-ID", required = false) String userId,
        @PathVariable("orderId") Long orderId
    ) {
        if (userId == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }

        MemberInfo memberInfo = memberFacade.getMember(userId);

        if (memberInfo == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }

        var query = new QueryOrderDetailUseCase.Query(orderId, memberInfo);
        var result = queryOrderDetailUseCase.execute(query);

        return ApiResponse.success(OrderV1Dto.OrderResponse.from(result.orderInfo()));
    }
}
