package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentFacade;
import com.loopers.application.payment.dto.SyncPaymentCommand;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentV1Controller implements PaymentV1ApiSpec {

    private final PaymentFacade paymentFacade;

    @PostMapping("/callback")
    @Override
    public ApiResponse<Boolean> handlePaymentCallback(@RequestBody PaymentV1Dto.PaymentCallbackRequest request) {
        log.warn("{} req, handlePaymentCallback", request);
        paymentFacade.syncPayment(
            new SyncPaymentCommand(
                request.transactionKey(),
                request.orderId(),
                request.cardType(),
                request.cardNo(),
                request.amount(),
                request.status(),
                request.reason()
            )
        );
        return ApiResponse.success(true);
    }
}
