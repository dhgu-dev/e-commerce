package com.loopers.domain.payment;

import com.loopers.domain.member.MemberModel;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PointPaymentProcessor implements PaymentProcessor<PointPaymentRequest> {

    private final PointManager pointManager;
    private final PaymentRepository paymentRepository;

    @Override
    public boolean supports(PaymentType type) {
        return type == PaymentType.POINTS;
    }

    @Transactional
    @Override
    public PaymentModel pay(PointPaymentRequest request) {
        MemberModel member = request.getMember();
        PaymentModel paymentModel = PaymentModel.createPointPayment(
            UUID.randomUUID().toString().replace("-", "").substring(0, 6),
            request.getMember().getUserId(),
            request.getOrder().getId().toString(),
            request.getAmount()
        );

        try {
            member.usePoints(request.getAmount().longValue());
        } catch (CoreException e) {
            if (e.getErrorType() != ErrorType.CONFLICT) {
                throw e;
            }
            paymentModel.failed(e.getMessage());
        }
        pointManager.updateMemberPoints(member);
        paymentModel.approve();
        return paymentRepository.save(paymentModel);
    }
}
