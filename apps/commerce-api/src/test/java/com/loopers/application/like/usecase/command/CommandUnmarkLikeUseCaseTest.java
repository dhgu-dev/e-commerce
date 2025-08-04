package com.loopers.application.like.usecase.command;

import com.loopers.application.member.MemberInfo;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.member.enums.Gender;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CommandUnmarkLikeUseCaseTest {

    @Autowired
    private CommandUnmarkLikeUseCase commandUnmarkLikeUseCase;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Test
    @Sql(statements = {
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (1, '테스트상품1', 1000, 15, 1, 3, '2023-10-01 00:00:00', '2023-10-01 00:00:00', NULL)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at) VALUES (1, 'testUser1', 'MALE', 'test@test.com', '2024-01-01', 0, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at) VALUES (2, 'testUser2', 'MALE', 'test@test.com', '2024-01-01', 0, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL)",
        "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at) VALUES (3, 'testUser3', 'MALE', 'test@test.com', '2024-01-01', 0, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL)",
        "insert into likes (created_at, member_id, product_id) values ('2023-10-01 12:00:00', 1, 1);",
        "insert into likes (created_at, member_id, product_id) values ('2023-10-01 12:00:00', 2, 1);",
        "insert into likes (created_at, member_id, product_id) values ('2023-10-01 12:00:00', 3, 1);"
    })
    void 멱등성_동시에_요청해도_같은_결과를_반환한다() throws InterruptedException {
        int threadCount = 5;
        MemberInfo memberInfo = new MemberInfo(
            1L,
            "testUser1",
            Gender.MALE,
            LocalDate.of(2024, 1, 1),
            "test@test.com",
            0L
        );
        Long productId = 1L;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    commandUnmarkLikeUseCase.execute(new CommandUnmarkLikeUseCase.Command(memberInfo, productId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long likeCount = likeRepository.getProductLikeCount(productId);

        assertThat(successCount.get()).isEqualTo(threadCount);
        assertThat(likeCount).isEqualTo(2);
    }
}
