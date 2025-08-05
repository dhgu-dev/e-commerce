package com.loopers.application.orders.usecase.command;

import com.loopers.application.member.MemberInfo;
import com.loopers.application.orders.dto.OrderInfo;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberService;
import com.loopers.domain.orders.ExternalServiceOutputPort;
import com.loopers.domain.orders.OrderService;
import com.loopers.domain.orders.OrdersModel;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandOrderUseCase {

    private final MemberService memberService;
    private final ProductService productService;
    private final OrderService orderService;
    private final ExternalServiceOutputPort deliveryClient;
    private final ProductRepository productRepository;

    @Transactional()
    public Result execute(Command command) {
        MemberModel member = memberService.getMember(command.memberInfo().userId());

        List<Pair<ProductModel, Long>> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        // 각 주문 상품 재고 차감 및 가격 합산
        for (int i = 0; i < command.productIds().size(); i++) {
            Long productId = command.productIds().get(i);
            long quantity = command.quantities().get(i);

            ProductModel product = productRepository.findWithLock(productId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "Product not found with ID: " + productId)
            );
            productService.decreaseStock(product, quantity);
            items.add(Pair.of(product, quantity));

            totalPrice = totalPrice.add(product.getPrice().multiply(quantity).getAmount());
        }

        // 포인트 차감
        memberService.payment(member, totalPrice);

        // 주문 상품 정보 저장
        OrdersModel order = orderService.order(member, items);

        // 주문 정보 전송
        deliveryClient.send(order);

        return new Result(OrderInfo.from(order));
    }

    public record Command(MemberInfo memberInfo, List<Long> productIds, List<Long> quantities) {
    }

    public record Result(OrderInfo orderInfo) {
    }
}
