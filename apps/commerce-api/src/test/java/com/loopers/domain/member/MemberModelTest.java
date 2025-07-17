package com.loopers.domain.member;

import com.loopers.domain.member.enums.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberModelTest {

    @DisplayName("회원 모델을 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("아이디와 성별과 생일과 이메일이 모두 주어지면, 정상적으로 생성된다.")
        @Test
        void createMemberModel_whenUserIdAndGenderAndBirthdateAndEmailAreProvided() {
            String userId = "test";
            Gender gender = Gender.MALE;
            String birthdate = "2020-01-01";
            String email = "test@test.com";

            MemberModel memberModel = new MemberModel(userId, gender, birthdate, email);

            assertAll(
                    () -> assertThat(memberModel.getId()).isNotNull(),
                    () -> assertThat(memberModel.getUserId()).isEqualTo(userId),
                    () -> assertThat(memberModel.getGender()).isEqualTo(gender),
                    () -> assertThat(memberModel.getBirthdate()).isEqualTo(
                            LocalDate.of(2020, 1, 1)
                    ),
                    () -> assertThat(memberModel.getEmail()).isEqualTo(email),
                    () -> assertThat(memberModel.getPoints()).isEqualTo(0L)
            );
        }

        @DisplayName("아이디가 빈칸 혹은 null 이면, BAD_REQUEST 예외가 발생한다")
        @Test
        void throwsBadRequestException_whenUserIdIsNullOrBlank() {
            String blankUserId = "     ";

            CoreException resultWhenNull = assertThrows(CoreException.class, () -> {
                new MemberModel(null, Gender.MALE, "2020-01-01", "test@test.com");
            });
            assertThat(resultWhenNull.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);

            CoreException resultWhenBlank = assertThrows(CoreException.class, () -> {
                new MemberModel(blankUserId, Gender.MALE, "2020-01-01", "test@test.com");
            });
            assertThat(resultWhenBlank.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("아이디가 영문 및 숫자 10자 이내 형식에 맞지 않으면, Member 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenUserIdIsNotAlphanumericOrExceedsTenCharacters() {
            String lengthExceedsTen = "abcde12345678910";
            String isNotAlphanumeric = "!@#한글";

            CoreException resultWhenLengthExceedsTen = assertThrows(CoreException.class, () -> {
                new MemberModel(lengthExceedsTen, Gender.MALE, "2020-01-01", "test@test.com");
            });
            assertThat(resultWhenLengthExceedsTen.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);

            CoreException resultWhenIsNotAlphanumeric = assertThrows(CoreException.class, () -> {
                new MemberModel(isNotAlphanumeric, Gender.MALE, "2020-01-01", "test@test.com");
            });
            assertThat(resultWhenIsNotAlphanumeric.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, Member 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenEmailIsNotValid() {
            CoreException result = assertThrows(CoreException.class, () -> {
                new MemberModel("test", Gender.MALE, "2020-01-01", "@not.email.com");
            });
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않으면, Member 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenBirthdateIsNotValid() {
            CoreException result = assertThrows(CoreException.class, () -> {
                new MemberModel("test", Gender.MALE, "20/01/01T05:30", "test@test.com");
            });
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("포인트 충전을 할 때")
    @Nested
    class ChargePoints {
        @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
        @ParameterizedTest
        @ValueSource(longs = {0L, -1L, -100L})
        void throwsBadRequestException_whenChargeAmountIsNegative(Long points) {
            MemberModel member = new MemberModel("test", Gender.MALE, "2020-01-01", "test@test.com");

            CoreException result = assertThrows(CoreException.class, () -> {
                member.chargePoints(points);
            });
            
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
