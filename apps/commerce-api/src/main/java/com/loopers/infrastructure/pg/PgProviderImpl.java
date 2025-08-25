package com.loopers.infrastructure.pg;

import com.loopers.domain.payment.*;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.support.resilience.ResilienceTemplate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PgProviderImpl implements PgProvider<CardPaymentRequest> {

    private static final Logger logger = LoggerFactory.getLogger(PgProviderImpl.class);
    private static final String CALLBACK_URL = "http://localhost:8080/api/v1/payments/callback";

    private final PgServiceClient pgServiceClient;
    private final ResilienceTemplate resilienceTemplate;

    @Override
    public TransactionResult requestTransaction(CardPaymentRequest request) {
        return TransactionResponse.toResult(resilienceTemplate.circuitBreaker(
            "requestTransaction",
            builder -> {
            }, // 기본 설정 사용
            () -> {
                var res = pgServiceClient.requestTransaction(
                    request.getMember().getUserId(),
                    new TransactionRequest(
                        OrderKeyEncoder.encode(request.getOrder().getId()),
                        TransactionRequest.CardTypeDto.from(request.getCardType()),
                        request.getCardNo(),
                        request.getAmount().longValue(),
                        CALLBACK_URL
                    )
                );
                logger.info("### pgServiceClient.requestTransaction: {} ###", res);
                return res.getData();
            },
            ex -> {
                logger.warn("circuitBreaker exception during requestTransaction. {}", ex.e().getMessage());
                throw new CoreException(ErrorType.SERVICE_UNAVAILABLE, "PG 시스템 장애로 인해 잠시 간 거래 요청을 처리할 수 없습니다.");
            }
        ));
    }

    @Override
    public TransactionDetailResult getTransactionDetail(String userId, String transactionKey) {
        return TransactionDetailResponse.toResult(resilienceTemplate.circuitBreaker(
            "getTransactionDetail",
            builder -> {
            }, // 기본 설정 사용
            () -> {
                var res = pgServiceClient.getTransactionDetail(userId, transactionKey);
                logger.info("### Transaction detail: {} ###", res);
                return res.getData();
            },
            ex -> {
                logger.warn("circuitBreaker exception during getTransactionDetail. {}", ex.e().getMessage());
                throw new CoreException(ErrorType.SERVICE_UNAVAILABLE, "PG 시스템 장애로 인해 잠시 거래 상세 정보를 가져올 수 없습니다.");
            }
        ));
    }

    @Override
    public OrderResult getTransactionsByOrder(String userId, String orderId) {
        return OrderResponse.toResult(resilienceTemplate.circuitBreaker(
            "getTransactionsByOrder",
            builder -> {
            }, // 기본 설정 사용
            () -> {
                var res = pgServiceClient.getTransactionsByOrder(userId, OrderKeyEncoder.encode(Long.parseLong(orderId)));
                logger.info("### Transactions by order: {} ###", res);
                return res.getData();
            },
            ex -> {
                logger.warn("circuitBreaker exception during getTransactionsByOrder. {}", ex.e().getMessage());
                throw new CoreException(ErrorType.SERVICE_UNAVAILABLE, "PG 시스템 장애로 인해 잠시 주문 거래 정보를 가져올 수 없습니다.");
            }
        ));
    }
}
