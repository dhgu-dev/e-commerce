package com.loopers.application.orders.usecase.command;

import com.loopers.application.member.MemberInfo;
import com.loopers.application.orders.usecase.command.CommandOrderUseCase.Command;
import com.loopers.application.orders.usecase.command.CommandOrderUseCase.Result;
import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberRepository;
import com.loopers.domain.orders.OrderRepository;
import com.loopers.domain.orders.OrdersModel;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CommandOrderUseCaseTest {

    @Autowired
    private CommandOrderUseCase commandOrderUseCase;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Test
    @Sql(statements = {
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (1, '테스트상품1', 1000, 15, 1, 10, '2023-10-01 00:00:00', '2023-10-01 00:00:00', NULL)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (1, 'testUser1', 'MALE', 'test@test.com', '2024-01-01', 10000, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (2, 'testUser2', 'MALE', 'test@test.com', '2024-01-01', 10000, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (3, 'testUser3', 'MALE', 'test@test.com', '2024-01-01', 10000, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (4, 'testUser4', 'MALE', 'test@test.com', '2024-01-01', 10000, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (5, 'testUser5', 'MALE', 'test@test.com', '2024-01-01', 10000, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)"
    })
    void 동일한_상품에_대해_여러_주문이_동시에_요청되어도_재고가_정상적으로_차감되어야_한다() throws InterruptedException {
        final int threadCount = 5;
        MemberModel m1 = memberRepository.findByUserId("testUser1").orElseThrow();
        MemberModel m2 = memberRepository.findByUserId("testUser2").orElseThrow();
        MemberModel m3 = memberRepository.findByUserId("testUser3").orElseThrow();
        MemberModel m4 = memberRepository.findByUserId("testUser4").orElseThrow();
        MemberModel m5 = memberRepository.findByUserId("testUser5").orElseThrow();

        List<MemberInfo> memberInfos = List.of(MemberInfo.from(m1), MemberInfo.from(m2), MemberInfo.from(m3), MemberInfo.from(m4), MemberInfo.from(m5));
        List<Long> productIds = List.of(1L);
        List<Long> quantities = List.of(2L);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            final int memberIndex = i % memberInfos.size();
            executor.submit(() -> {
                try {
                    commandOrderUseCase.execute(new Command(memberInfos.get(memberIndex), productIds, quantities, null));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        ProductModel productModel = productRepository.find(1L).orElseThrow();
        assertThat(productModel.getStock().getQuantity()).isEqualTo(5L);

        assertThat(failureCount.get()).isEqualTo(0);
        assertThat(successCount.get()).isEqualTo(threadCount);
    }

    @Test
    @Sql(statements = {
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (1, '테스트상품1', 1000, 15, 1, 10, '2023-10-01 00:00:00', '2023-10-01 00:00:00', NULL)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (1, 'testUser1', 'MALE', 'test@test.com', '2024-01-01', 10000, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)",
        "insert into coupon (id, amount, rate, created_at, deleted_at, issued_at, member_id, code, discount_type, target_scope, version) values (1, 500, null, '2023-10-01 12:00:00', null, '2023-10-01 12:00:00', 1, 'COUPON_12345', 'FIXED_AMOUNT', 'ORDER', 0)"
    })
    void 동일한_쿠폰으로_여러_기기에서_동시에_주문해도_쿠폰은_단_한번만_사용되어야_한다() throws InterruptedException {
        final int threadCount = 5;
        MemberModel member = memberRepository.findByUserId("testUser1").orElseThrow();
        Long couponId = 1L;

        List<Long> productIds = List.of(1L);
        List<Long> quantities = List.of(1L);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    commandOrderUseCase.execute(new Command(MemberInfo.from(member), productIds, quantities, couponId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        CouponModel couponModel = couponRepository.find(couponId).orElseThrow();
        List<OrdersModel> orders = orderRepository.searchByCoupon(couponId);

        assertThat(couponModel.getDeletedAt()).isNotNull();
        assertThat(orders.size()).isEqualTo(1);
        assertThat(successCount.get()).isEqualTo(1);
    }

    @Test
    @Sql(statements = {
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (1, '테스트상품1', 1000, 15, 1, 10, '2023-10-01 00:00:00', '2023-10-01 00:00:00', NULL)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (1, 'testUser1', 'MALE', 'test@test.com', '2024-01-01', 10000, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)",
        "insert into coupon (id, amount, rate, created_at, deleted_at, issued_at, member_id, code, discount_type, target_scope, version) values (1, 500, null, '2023-10-01 12:00:00', '2023-10-02 12:00:00', '2023-10-01 12:00:00', 1, 'USED_COUPON', 'FIXED_AMOUNT', 'ORDER', 0)"
    })
    void 사용할_수_없는_쿠폰으로_주문하면_실패해야_한다() {
        // given
        MemberModel member = memberRepository.findByUserId("testUser1").orElseThrow();
        Command commandWithUsedCoupon = new Command(MemberInfo.from(member), List.of(1L), List.of(1L), 1L);
        Command commandWithNonExistentCoupon = new Command(MemberInfo.from(member), List.of(1L), List.of(1L), 999L);

        // when & then
        assertThrows(CoreException.class, () -> {
            commandOrderUseCase.execute(commandWithUsedCoupon);
        });
        assertThrows(CoreException.class, () -> {
            commandOrderUseCase.execute(commandWithNonExistentCoupon);
        });
    }

    @Test
    @Sql(statements = {
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (1, '재고부족상품', 1000, 1, 1, 10, '2023-10-01 00:00:00', '2023-10-01 00:00:00', NULL)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (1, 'testUser1', 'MALE', 'test@test.com', '2024-01-01', 10000, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)"
    })
    void 재고가_부족하면_주문은_실패해야_한다() {
        // given
        MemberModel member = memberRepository.findByUserId("testUser1").orElseThrow();
        // 재고(1)보다 많은 수량(2)을 주문
        Command command = new Command(MemberInfo.from(member), List.of(1L), List.of(2L), null);

        // when & then
        assertThrows(CoreException.class, () -> {
            commandOrderUseCase.execute(command);
        });
    }

    @Test
    @Sql(statements = {
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (1, '성공테스트상품', 1000, 10, 1, 10, '2023-10-01 00:00:00', '2023-10-01 00:00:00', NULL)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (1, 'successUser', 'MALE', 'success@test.com', '2024-01-01', 10000, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)"
    })
    void 주문에_성공하면_재고가_정상적으로_차감되고_주문이_생성되어야_한다() {
        // given
        MemberModel member = memberRepository.findByUserId("successUser").orElseThrow();
        Command command = new Command(MemberInfo.from(member), List.of(1L), List.of(2L), null, null);

        // when
        Result result = commandOrderUseCase.execute(command);

        // then
        // 주문 생성 확인
        assertThat(result.orderInfo().id()).isNotNull();
        OrdersModel order = orderRepository.find(result.orderInfo().id()).orElseThrow();
        assertThat(order.getMemberId()).isEqualTo(member.getId());
        assertThat(order.getItems()).hasSize(1);

        // 재고 차감 확인
        ProductModel productAfter = productRepository.find(1L).orElseThrow();
        assertThat(productAfter.getStock().getQuantity()).isEqualTo(8L); // 10 - 2
    }
}
