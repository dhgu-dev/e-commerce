package com.loopers.application.orders.usecase.query;

import com.loopers.application.member.MemberInfo;
import com.loopers.application.orders.dto.OrderInfo;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberService;
import com.loopers.domain.orders.OrderService;
import com.loopers.domain.orders.OrdersModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryMemberOrdersUseCase {

    private final OrderService orderService;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public Result execute(Query query) {
        MemberModel member = memberService.getMember(query.memberInfo().userId());

        List<OrdersModel> orders = orderService.getOrders(member);

        return new Result(
            orders.stream()
                .map(OrderInfo::from)
                .toList()
        );
    }

    public record Query(MemberInfo memberInfo) {
    }

    public record Result(List<OrderInfo> orders) {
    }

}
