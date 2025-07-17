package com.loopers.application.member;

import com.loopers.application.points.PointsFacade;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberService;
import com.loopers.domain.member.enums.Gender;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

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

    @DisplayName("보유 포인트를 조회할 때")
    @Nested
    class GetPoints {

        @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void returnsPoints_whenExistUserId() {
            String userId = "testUser";
            Long initialPoints = 1000L;
            doReturn(new MemberModel(userId, Gender.FEMALE, "2000-01-01", "test@test.com", initialPoints)).when(memberService).getMember(userId);

            Long points = pointsFacade.getPoints(userId);

            assertThat(points).isEqualTo(initialPoints);
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void returnsNull_whenNotExistUserId() {
            String notExistUserId = "notTestUser";
            doThrow(new CoreException(ErrorType.NOT_FOUND))
                    .when(memberService).getMember(eq(notExistUserId));

            Long points = pointsFacade.getPoints(notExistUserId);

            assertThat(points).isNull();
        }
    }
}
