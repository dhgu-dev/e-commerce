package com.loopers.application.orders.usecase.command;

import com.loopers.application.member.MemberInfo;
import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberRepository;
import com.loopers.domain.orders.OrderRepository;
import com.loopers.domain.orders.OrdersModel;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
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
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (2, '테스트상품2', 2000, 10, 1, 10, '2023-10-01 00:00:00', '2023-10-01 00:00:00', NULL)",
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (3, '테스트상품3', 3000, 5, 1, 10, '2023-10-01 00:00:00', '2023-10-01 00:00:00', NULL)",
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (4, '테스트상품4', 4000, 5, 1, 10, '2023-10-01 00:00:00', '2023-10-01 00:00:00', NULL)",
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (5, '테스트상품5', 5000, 5, 1, 10, '2023-10-01 00:00:00', '2023-10-01 00:00:00', NULL)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (1, 'testUser', 'MALE', 'test@test.com', '2024-01-01', 20000, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)"
    })
    void 동일한_유저가_서로_다른_주문을_동시에_수행해도_포인트가_정상적으로_차감되어야_한다() throws InterruptedException {
        final int threadCount = 5;
        MemberInfo memberInfo = MemberInfo.from(memberRepository.findByUserId("testUser").orElseThrow());
        List<Long> productIds = List.of(1L, 2L, 3L, 4L, 5L);
        List<Long> quantities = List.of(1L, 1L, 1L, 1L, 1L);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            final int itemIndex = i % productIds.size();
            executor.submit(() -> {
                try {
                    commandOrderUseCase.execute(new CommandOrderUseCase.Command(memberInfo, List.of(productIds.get(itemIndex)), List.of(quantities.get(itemIndex)), null));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        MemberModel member = memberRepository.findByUserId("testUser").orElseThrow();

        assertThat(successCount.get()).isEqualTo(threadCount);
        assertThat(member.getPoints()).isEqualTo(5000L);
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
                    commandOrderUseCase.execute(new CommandOrderUseCase.Command(memberInfos.get(memberIndex), productIds, quantities, null));
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
                    commandOrderUseCase.execute(new CommandOrderUseCase.Command(MemberInfo.from(member), productIds, quantities, couponId));
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
}
