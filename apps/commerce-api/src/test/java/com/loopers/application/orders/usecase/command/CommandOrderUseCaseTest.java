package com.loopers.application.orders.usecase.command;

import com.loopers.application.member.MemberInfo;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberRepository;
import com.loopers.domain.member.enums.Gender;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
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
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (1, 'testUser', 'MALE', 'test@test.com', '2024-01-01', 10000, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)"
    })
    void 동일한_유저가_여러_기기에서_동시에_주문해도_포인트가_중복_차감되지_않아야_한다() throws InterruptedException {
        int threadCount = 20;
        MemberInfo memberInfo = new MemberInfo(
            1L,
            "testUser",
            Gender.MALE,
            LocalDate.of(2024, 1, 1),
            "test@test.com",
            10000L
        );
        List<Long> productIds = List.of(1L, 2L, 3L);
        List<Long> quantities = List.of(1L, 1L, 1L);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    commandOrderUseCase.execute(new CommandOrderUseCase.Command(memberInfo, productIds, quantities));
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

        assertThat(failureCount.get()).isEqualTo(19);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(member.getPoints()).isEqualTo(4000L);
    }
}
