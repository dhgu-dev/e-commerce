package com.loopers.application.like.usecase.command;

import com.loopers.application.member.MemberInfo;
import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.like.LikeEventPublisher;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberService;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommandMarkLikeUseCase {

    private final MemberService memberService;
    private final ProductService productService;
    private final LikeService likeService;
    private final LikeEventPublisher likeEventPublisher;

    @Transactional
    public void execute(Command command) {
        if (command == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Command cannot be null");
        }

        MemberModel member = memberService.getMember(command.memberInfo.userId());
        ProductModel product = productService.getDetail(command.productId());

        try {
            likeService.like(member, product);
        } catch (DataIntegrityViolationException e) {
        }

        likeEventPublisher.publish(new LikeEvent.LikeMarkedEvent(member.getId(), product.getId()));
    }

    public record Command(MemberInfo memberInfo, Long productId) {
    }
}
