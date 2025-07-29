package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BrandModelTest {

    @DisplayName("브랜드 모델을 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("이름이 빈칸 혹은 null 이면, BAD_REQUEST 예외가 발생한다")
        @ParameterizedTest
        @NullAndEmptySource
        void throwsBadRequestException_whenNameIsNullOrBlank(String name) {
            CoreException result = assertThrows(CoreException.class, () -> {
                new BrandModel(name, "Test Description");
            });

            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("설명이 빈칸이면, BAD_REQUEST 예외가 발생한다")
        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        void throwsBadRequestException_whenDescriptionIsBlank(String description) {
            String name = "Test Brand";

            CoreException result = assertThrows(CoreException.class, () -> {
                BrandModel brandModel = new BrandModel(name, description);
            });

            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
