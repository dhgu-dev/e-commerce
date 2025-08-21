package com.loopers.infrastructure.pg;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pg-service", url = "http://localhost:8082")
public interface PgServiceClient {

    Logger log = LoggerFactory.getLogger(PgServiceClient.class);

    @PostMapping("/api/v1/payments")
    @Retry(name = "requestTransaction", fallbackMethod = "fallbackRequestTransaction")
    PgApiResponse<TransactionResponse> requestTransaction(
        @RequestHeader("X-USER-ID") String userId,
        @RequestBody() TransactionRequest body
    );

    @GetMapping("/api/v1/payments/{transactionKey}")
    @Retry(name = "getTransactionDetail", fallbackMethod = "fallbackGetTransactionDetail")
    PgApiResponse<TransactionDetailResponse> getTransactionDetail(
        @RequestHeader("X-USER-ID") String userId,
        @PathVariable("transactionKey") String transactionKey
    );

    @GetMapping("/api/v1/payments")
    @Retry(name = "getTransactionsByOrder", fallbackMethod = "fallbackGetTransactionsByOrder")
    PgApiResponse<OrderResponse> getTransactionsByOrder(
        @RequestHeader("X-USER-ID") String userId,
        @RequestParam("orderId") String orderId
    );

    default PgApiResponse<TransactionResponse> fallbackRequestTransaction(String userId, TransactionRequest body, Throwable e) {
        log.error("리트라이 실패 - PG 시스템 장애로 인해 폴백 로직 실행: " + e.getMessage() + " for userId: " + userId);
        throw new CoreException(ErrorType.SERVICE_UNAVAILABLE, "리트라이 실패 - PG 시스템 장애로 인해 잠시 간 거래 요청을 처리할 수 없습니다.");
    }

    default PgApiResponse<TransactionDetailResponse> fallbackGetTransactionDetail(String userId, String transactionKey, Throwable e) {
        log.error("리트라이 실패 - PG 시스템 장애로 인해 폴백 로직 실행: " + e.getMessage() + " for userId: " + userId);
        throw new CoreException(ErrorType.SERVICE_UNAVAILABLE, "리트라이 실패 - PG 시스템 장애로 인해 잠시 거래 상세 정보를 가져올 수 없습니다.");
    }

    default PgApiResponse<OrderResponse> fallbackGetTransactionsByOrder(String userId, String orderId, Throwable e) {
        log.error("리트라이 실패 - PG 시스템 장애로 인해 폴백 로직 실행: " + e.getMessage() + " for userId: " + userId);
        throw new CoreException(ErrorType.SERVICE_UNAVAILABLE, "리트라이 실패 - PG 시스템 장애로 인해 잠시 주문 거래 정보를 가져올 수 없습니다.");
    }

}
