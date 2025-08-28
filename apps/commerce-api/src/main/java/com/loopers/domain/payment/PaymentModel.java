package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class PaymentModel extends BaseEntity {
    @Column(name = "transaction_key", nullable = false, unique = true)
    @Getter
    String transactionKey;

    @Column(name = "user_id", nullable = false)
    @Getter
    String userId;

    @Column(name = "order_id", nullable = false)
    @Getter
    String orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    CardType cardType;

    @Column(name = "card_no")
    String cardNo;

    @Column(name = "amount", nullable = false)
    @Getter
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Getter
    TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "reason")
    String reason = null;

    private PaymentModel(
        String transactionKey,
        String userId,
        String orderId,
        PaymentType paymentType,
        CardType cardType,
        String cardNo,
        BigDecimal amount,
        TransactionStatus status,
        String reason
    ) {
        this.transactionKey = transactionKey;
        this.userId = userId;
        this.orderId = orderId;
        this.paymentType = paymentType;
        this.cardType = cardType;
        this.cardNo = cardNo;
        this.amount = amount;
        this.status = status;
        this.reason = reason;
    }

    public static PaymentModel createCardPayment(
        String transactionKey,
        String userId,
        String orderId,
        CardType cardType,
        String cardNo,
        BigDecimal amount
    ) {
        return new PaymentModel(
            transactionKey,
            userId,
            orderId,
            PaymentType.CARD,
            cardType,
            cardNo,
            amount,
            TransactionStatus.PENDING,
            null
        );
    }

    public static PaymentModel createPointPayment(
        String transactionKey,
        String userId,
        String orderId,
        BigDecimal amount
    ) {
        return new PaymentModel(
            transactionKey,
            userId,
            orderId,
            PaymentType.POINTS,
            null,
            null,
            amount,
            TransactionStatus.PENDING,
            null
        );
    }

    public void approve() {
        if (status != TransactionStatus.PENDING) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "결제승인은 대기상태에서만 가능합니다.");
        }
        this.status = TransactionStatus.SUCCESS;
        this.reason = "정상 승인되었습니다.";
    }

    public void failed(String reason) {
        if (status != TransactionStatus.PENDING) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "결제실패는 대기상태에서만 가능합니다.");
        }
        this.status = TransactionStatus.FAILED;
        this.reason = reason != null ? reason : "결제에 실패했습니다. 다시 시도해주세요.";
    }
}
