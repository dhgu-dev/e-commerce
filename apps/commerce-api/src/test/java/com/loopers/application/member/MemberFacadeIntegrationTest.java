package com.loopers.application.member;

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
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MemberFacadeIntegrationTest {

    @Autowired
    private MemberFacade memberFacade;

    @MockitoSpyBean
    private MemberService memberService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("회원가입을 할 때")
    @Nested
    class SignUp {
        @DisplayName("회원 가입시 User 저장이 수행된다. ( spy 검증 )")
        @Test
        void savesUser_whenSignUpIsCalled() {
            String userId = "testUser";
            Gender gender = Gender.MALE;
            String birthdate = "2000-01-01";
            String email = "test@test.com";

            MemberInfo memberInfo = memberFacade.signUp(userId, gender, birthdate, email);

            verify(memberService).createMember(userId, gender, birthdate, email);

            assertThat(memberInfo.userId()).isEqualTo(userId);
            assertThat(memberInfo.gender()).isEqualTo(gender);
            assertThat(memberInfo.birthdate()).isEqualTo(birthdate);
            assertThat(memberInfo.email()).isEqualTo(email);
        }

        @DisplayName("이미 가입된 ID 로 회원가입 시도 시, 실패한다.")
        @Test
        @Sql(statements = {
            "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (1, 'testUser', 'MALE', 'test@test.com', '2024-01-01', 0, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)"
        })
        void throwsException_whenUserIdAlreadyExists() {
            CoreException exception = assertThrows(CoreException.class, () -> memberFacade.signUp("testUser", Gender.MALE, "2024-01-01", "test@test.com"));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }
    }

    @DisplayName("내 정보 조회를 할 때")
    @Nested
    class GetMember {
        @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        @Sql(statements = {
            "INSERT INTO member (id, user_id, gender, email, birthdate, points, created_at, updated_at, deleted_at, version) VALUES (1, 'testUser', 'MALE', 'test@test.com', '2024-01-01', 0, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL, 0)"
        })
        void returnMember_whenExistUserId() {
            MemberInfo target = memberFacade.getMember("testUser");

            assertThat(target).isEqualTo(new MemberInfo(1L, "testUser", Gender.MALE, LocalDate.of(2024, 1, 1), "test@test.com", 0L));
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void returnNull_whenNotExistUserId() {
            MemberInfo target = memberFacade.getMember("nonExistentUserId");
            assertNull(target);
        }
    }
}
