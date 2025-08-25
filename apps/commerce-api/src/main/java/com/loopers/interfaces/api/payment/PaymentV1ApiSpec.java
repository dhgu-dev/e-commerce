package com.loopers.interfaces.api.payment;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Payment V1 API")
public interface PaymentV1ApiSpec {
    @Operation(
        summary = "결제 콜백 핸들러"
    )
    ApiResponse<Boolean> handlePaymentCallback(
        PaymentV1Dto.PaymentCallbackRequest request
    );
}
