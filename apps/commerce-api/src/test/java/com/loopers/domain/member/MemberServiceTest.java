package com.loopers.domain.member;

import com.loopers.domain.member.enums.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @DisplayName("회원을 조회할 때")
    @Nested
    class GetMember {
        @DisplayName("회원 아이디를 제공하면 회원 모델을 반환한다")
        @Test
        void returnsMemberModel_whenUserIdIsProvided() {
            String userId = "testUser";
            MemberModel memberModel = new MemberModel(userId, Gender.MALE, "1990-01-01", "test@test.com");

            when(memberRepository.findByUserId(anyString())).thenReturn(Optional.of(memberModel));
            MemberModel expectedMember = memberService.getMember(userId);

            verify(memberRepository).findByUserId(userId);
            assertThat(expectedMember.getUserId()).isEqualTo(userId);
        }

        @DisplayName("없는 회원 아이디를 제공하면 예외를 던진다")
        @Test
        void throwsException_whenUserNotFound() {
            String notExistingUserId = "nonExistentUser";

            when(memberRepository.findByUserId(anyString())).thenReturn(Optional.empty());

            CoreException exception = assertThrows(CoreException.class, () -> memberService.getMember(notExistingUserId));

            verify(memberRepository).findByUserId(notExistingUserId);
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("회원을 생성할 때")
    @Nested
    class CreateMember {
        @DisplayName("회원 아이디, 성별, 생일, 이메일을 제공하면 회원 모델을 생성한다")
        @Test
        void createMemberModel_whenAllFieldsAreProvided() {
            String userId = "newUser";
            Gender gender = Gender.MALE;
            String birthdate = "1995-05-05";
            String email = "test@test.com";

            MemberModel newMember = new MemberModel(userId, gender, birthdate, email);
            when(memberRepository.create(any(MemberModel.class))).thenReturn(newMember);

            MemberModel createdMember = memberService.createMember(userId, gender, birthdate, email);

            verify(memberRepository).create(any(MemberModel.class));
            assertAll(
                () -> assertThat(createdMember.getUserId()).isEqualTo(userId),
                () -> assertThat(createdMember.getGender()).isEqualTo(gender),
                () -> assertThat(createdMember.getBirthdate()).isEqualTo(birthdate),
                () -> assertThat(createdMember.getEmail()).isEqualTo(email)
            );
        }
    }

    @DisplayName("포인트를 충전할 때")
    @Nested
    class ChargePoints {
        @DisplayName("없는 회원 아이디를 제공하면 예외를 던진다")
        @Test
        void throwsException_whenNotExistUserIdIsProvided() {
            String notExistUserId = "notExistUser";
            Long pointsToCharge = 100L;

            when(memberRepository.findByUserId(anyString())).thenReturn(Optional.empty());

            CoreException exception = assertThrows(CoreException.class, () -> memberService.chargePoints(notExistUserId, pointsToCharge));

            verify(memberRepository).findByUserId(notExistUserId);
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("회원 아이디와 포인트를 제공하면 해당 회원의 포인트를 충전한다")
        @Test
        void chargesPoints_whenUserIdAndPointsAreProvided() {
            String userId = "user";
            Long pointsToCharge = 1000L;
            MemberModel member = new MemberModel(userId, Gender.FEMALE, "2000-01-01", "test@test.com");

            when(memberRepository.findByUserId(userId)).thenReturn(Optional.of(member));
            when(memberRepository.update(any(MemberModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            MemberModel updatedMember = memberService.chargePoints(userId, pointsToCharge);

            verify(memberRepository).findByUserId(userId);
            verify(memberRepository).update(member);
            assertThat(updatedMember.getPoints()).isEqualTo(pointsToCharge);
        }
    }

    @DisplayName("포인트 결제(payment) 시")
    @Nested
    class Payment {

        @DisplayName("정상적으로 결제하면 포인트가 차감되고 update가 호출된다")
        @Test
        void payment_success() {
            MemberModel member = Mockito.spy(new MemberModel("user", Gender.FEMALE, "2000-01-01", "test@test.com", 1000L));
            BigDecimal totalPrice = BigDecimal.valueOf(500L);

            when(memberRepository.update(any(MemberModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            memberService.payment(member, totalPrice);

            verify(member).usePoints(500L);
            verify(memberRepository).update(member);
            assertThat(member.getPoints()).isEqualTo(500L);
        }

        @DisplayName("member가 null이면 BAD_REQUEST 예외 발생")
        @Test
        void payment_memberNull_throwsException() {
            CoreException ex = assertThrows(CoreException.class, () -> memberService.payment(null, BigDecimal.valueOf(100)));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("totalPrice가 null이면 BAD_REQUEST 예외 발생")
        @Test
        void payment_totalPriceNull_throwsException() {
            MemberModel member = new MemberModel("user", Gender.FEMALE, "2000-01-01", "test@test.com", 1000L);
            CoreException ex = assertThrows(CoreException.class, () -> memberService.payment(member, null));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("totalPrice가 0 이하이면 BAD_REQUEST 예외 발생")
        @Test
        void payment_totalPriceZeroOrNegative_throwsException() {
            MemberModel member = new MemberModel("user", Gender.FEMALE, "2000-01-01", "test@test.com", 1000L);

            CoreException ex1 = assertThrows(CoreException.class, () -> memberService.payment(member, BigDecimal.ZERO));
            assertThat(ex1.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);

            CoreException ex2 = assertThrows(CoreException.class, () -> memberService.payment(member, BigDecimal.valueOf(-100)));
            assertThat(ex2.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("포인트가 부족하면 CONFLICT 예외 발생")
        @Test
        void payment_insufficientPoints_throwsException() {
            MemberModel member = new MemberModel("user", Gender.FEMALE, "2000-01-01", "test@test.com", 100L);
            BigDecimal totalPrice = BigDecimal.valueOf(200L);

            CoreException ex = assertThrows(CoreException.class, () -> memberService.payment(member, totalPrice));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }
    }
}
