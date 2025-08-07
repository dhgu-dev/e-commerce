package com.loopers.application.orders.usecase.command;

import com.loopers.application.member.MemberInfo;
import com.loopers.application.orders.dto.OrderInfo;
import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberRepository;
import com.loopers.domain.member.MemberService;
import com.loopers.domain.orders.ExternalServiceOutputPort;
import com.loopers.domain.orders.OrderRepository;
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
    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

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
            if (coupon.getDeletedAt() != null) {
                throw new CoreException(ErrorType.BAD_REQUEST, "사용할 수 없는 쿠폰입니다.");
            }
            if (coupon.getIssuedAt() == null || !coupon.hasOwned(member.getId())) {
                throw new CoreException(ErrorType.BAD_REQUEST, "사용할 수 없는 쿠폰입니다.");
            }
        }

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

        // 쿠폰 사용
        if (coupon != null) {
            totalPrice = coupon.apply(totalPrice);
            couponRepository.saveAndFlush(coupon);
        }

        // 포인트 차감
        memberService.payment(member, totalPrice);

        // 주문 상품 정보 저장
        OrdersModel order = orderService.order(member, items, coupon != null ? coupon.getId() : null);
        order.process();
        OrdersModel result = orderRepository.save(order);

        // 주문 정보 전송
        try {
            deliveryClient.send(order);
        } catch (Exception e) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "Failed to process order: " + e.getMessage());
        }

        return new Result(OrderInfo.from(result));
    }

    public record Command(MemberInfo memberInfo, List<Long> productIds, List<Long> quantities, Long couponId) {
    }

    public record Result(OrderInfo orderInfo) {
    }
}
