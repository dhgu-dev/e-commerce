package com.loopers.application.orders.usecase.command;

import com.loopers.application.member.MemberInfo;
import com.loopers.application.orders.dto.CardInfo;
import com.loopers.application.orders.dto.OrderInfo;
import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberRepository;
import com.loopers.domain.orders.OrderEvent;
import com.loopers.domain.orders.OrderEventPublisher;
import com.loopers.domain.orders.OrderService;
import com.loopers.domain.orders.OrdersModel;
import com.loopers.domain.orders.vo.Price;
import com.loopers.domain.product.*;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommandOrderUseCase {

    private final ProductService productService;
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final ProductEventPublisher productEventPublisher;

    @Transactional()
    public Result execute(Command command) {
        MemberModel member = memberRepository.findWithLock(command.memberInfo().id()).orElseThrow(
            () -> new CoreException(ErrorType.NOT_FOUND, "Member not found with ID: " + command.memberInfo().id())
        );

        CouponModel coupon = null;
        if (command.couponId() != null) {
            coupon = couponRepository.find(command.couponId()).orElse(null);
            if (coupon == null) {
                throw new CoreException(ErrorType.NOT_FOUND, "Coupon not found with ID: " + command.couponId());
            }
            if (coupon.isUsed()) {
                throw new CoreException(ErrorType.BAD_REQUEST, "사용할 수 없는 쿠폰입니다.");
            }
            if (coupon.getIssuedAt() == null || !coupon.hasOwned(member.getId())) {
                throw new CoreException(ErrorType.BAD_REQUEST, "사용할 수 없는 쿠폰입니다.");
            }
        }

        List<Pair<ProductModel, Long>> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        // 각 주문 상품 가격 합산
        for (int i = 0; i < command.productIds().size(); i++) {
            Long productId = command.productIds().get(i);
            long quantity = command.quantities().get(i);

            ProductModel product = productRepository.findWithLock(productId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "Product not found with ID: " + productId)
            );

            items.add(Pair.of(product, quantity));
            totalPrice = totalPrice.add(product.getPrice().multiply(quantity).getAmount());
        }

        // 쿠폰 사용
        if (coupon != null) {
            totalPrice = coupon.apply(totalPrice);

        }

        // 재고 차감
        for (int i = 0; i < items.size(); i++) {
            ProductModel product = items.get(i).getFirst();
            long quantity = items.get(i).getSecond();

            productService.decreaseStock(product, quantity);
        }

        // 주문 정보 생성
        OrdersModel order = orderService.order(member, items, coupon != null ? coupon.getId() : null, Price.of(totalPrice));

        orderEventPublisher.publish(new OrderEvent.OrderCreatedEvent(
            order.getId(),
            order.getMemberId(),
            order.getItems().stream().map(item -> item.getProductSnapshot().getProductId()).toList(),
            order.getCouponId()
        ));

        for (var item : order.getItems()) {
            ProductModel product = productRepository.findWithLock(item.getProductSnapshot().getProductId()).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "Product not found with ID: " + item.getProductSnapshot().getProductId())
            );
            productEventPublisher.publish(new ProductEvent.StockAdjustedEvent(
                UUID.randomUUID().toString(),
                item.getProductSnapshot().getProductId(),
                product.getStock().getQuantity(),
                ZonedDateTime.now(),
                "StockAdjustedEvent",
                member.getId()
            ));
        }

        return new Result(OrderInfo.from(order));
    }

    public record Command(MemberInfo memberInfo, List<Long> productIds, List<Long> quantities, Long couponId, CardInfo cardInfo) {
        public Command(MemberInfo memberInfo, List<Long> productIds, List<Long> quantities, Long couponId) {
            this(memberInfo, productIds, quantities, couponId, null);
        }
    }

    public record Result(OrderInfo orderInfo) {
    }
}
