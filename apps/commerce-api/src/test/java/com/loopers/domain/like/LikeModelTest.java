package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LikeModelTest {

    @DisplayName("좋아요 모델을 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("회원 아이디 혹은 상품 아이디가 null 이면, BAD_REQUEST 예외가 발생한다")
        @Test
        void throwsBadRequestException_whenMemberIdOrProductIdIsNull() {
            Long memberId = 1L;
            Long productId = 1L;

            assertAll(
                    () -> {
                        CoreException exception = assertThrows(CoreException.class, () -> new LikeModel(null, productId));
                        assertEquals(ErrorType.BAD_REQUEST, exception.getErrorType());
                    },
                    () -> {
                        CoreException exception = assertThrows(CoreException.class, () -> new LikeModel(memberId, null));
                        assertEquals(ErrorType.BAD_REQUEST, exception.getErrorType());
                    },
                    () -> {
                        CoreException exception = assertThrows(CoreException.class, () -> new LikeModel(null, null));
                        assertEquals(ErrorType.BAD_REQUEST, exception.getErrorType());
                    }
            );
        }
    }

}
