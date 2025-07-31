package com.loopers.application.orders.usecase.query;

import com.loopers.application.member.MemberInfo;
import com.loopers.application.orders.dto.OrderInfo;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberService;
import com.loopers.domain.orders.OrderService;
import com.loopers.domain.orders.OrdersModel;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryOrderDetailUseCase {

    private final OrderService orderService;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public Result execute(Query query) {
        MemberModel member = memberService.getMember(query.memberInfo().userId());

        OrdersModel order = orderService.getDetail(query.orderId());

        if (!order.getMemberId().equals(query.memberInfo().id())) {
            throw new CoreException(ErrorType.FORBIDDEN, "다른 사용자의 주문입니다.");
        }

        return new Result(OrderInfo.from(order));
    }

    public record Query(Long orderId, MemberInfo memberInfo) {
    }

    public record Result(OrderInfo orderInfo) {
    }
}
