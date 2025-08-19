package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payment")
public class PaymentModel extends BaseEntity {
    @Column(name = "transaction_key", nullable = false, unique = true)
    String transactionKey;

    @Column(name = "user_id", nullable = false)
    String userId;

    @Column(name = "order_id", nullable = false)
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
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "reason")
    String reason = null;

    public PaymentModel(
        String transactionKey,
        String userId,
        String orderId,
        CardType cardType,
        String cardNo,
        BigDecimal amount,
        TransactionStatus status,
        String reason
    ) {
        this.transactionKey = transactionKey;
        this.userId = userId;
        this.orderId = orderId;
        this.paymentType = PaymentType.CARD;
        this.cardType = cardType;
        this.cardNo = cardNo;
        this.amount = amount;
        this.status = status;
        this.reason = reason;
    }

    public PaymentModel(
        String transactionKey,
        String userId,
        String orderId,
        BigDecimal amount,
        TransactionStatus status,
        String reason
    ) {
        this.transactionKey = transactionKey;
        this.userId = userId;
        this.orderId = orderId;
        this.paymentType = PaymentType.POINTS;
        this.cardType = null;
        this.cardNo = null;
        this.amount = amount;
        this.status = status;
        this.reason = reason;
    }

    void approve() {
        if (status != TransactionStatus.PENDING) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "결제승인은 대기상태에서만 가능합니다.");
        }
        status = TransactionStatus.SUCCESS;
        reason = "정상 승인되었습니다.";
    }

    void invalidCard() {
        if (status != TransactionStatus.PENDING) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "결제처리는 대기상태에서만 가능합니다.");
        }
        status = TransactionStatus.FAILED;
        reason = "잘못된 카드입니다. 다른 카드를 선택해주세요.";
    }

    void limitExceeded() {
        if (status != TransactionStatus.PENDING) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "한도초과 처리는 대기상태에서만 가능합니다.");
        }
        status = TransactionStatus.FAILED;
        reason = "한도초과입니다. 다른 카드를 선택해주세요.";
    }
}
