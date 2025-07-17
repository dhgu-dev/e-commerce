package com.loopers.application.member;

import com.loopers.application.points.PointsFacade;
import com.loopers.domain.member.MemberService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class PointsFacadeIntegrationTest {

    @Autowired
    private PointsFacade pointsFacade;

    @MockitoSpyBean
    private MemberService memberService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("포인트를 충전할 때")
    @Nested
    class ChargePoints {

        @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
        @Test
        void throwsException_whenNotExistUserId() {
            String notExistUserId = "notTestUser";
            Long points = 1000L;

            CoreException exception = assertThrows(CoreException.class, () -> pointsFacade.chargePoints(notExistUserId, points));

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
