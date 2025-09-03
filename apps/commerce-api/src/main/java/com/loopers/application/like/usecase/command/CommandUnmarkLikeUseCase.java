package com.loopers.application.like.usecase.command;

import com.loopers.application.member.MemberInfo;
import com.loopers.domain.brand.BrandEvent;
import com.loopers.domain.brand.BrandEventPublisher;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommandUnmarkLikeUseCase {

    private final MemberService memberService;
    private final ProductService productService;
    private final LikeService likeService;
    private final LikeEventPublisher likeEventPublisher;
    private final BrandEventPublisher brandEventPublisher;

    @Transactional
    public void execute(Command command) {
        if (command == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Command cannot be null");
        }

        MemberModel member = memberService.getMember(command.memberInfo.userId());
        ProductModel product = productService.getDetail(command.productId());

        likeService.unlike(member, product);

        likeEventPublisher.publish(new LikeEvent.LikeUnmarkedEvent(member.getId(), product.getId()));
        likeEventPublisher.publish(new LikeEvent.LikeChangedEvent(UUID.randomUUID().toString(), member.getId(), product.getId(), ZonedDateTime.now(), "LikeChangedEvent"));
        brandEventPublisher.publish(new BrandEvent.BrandProductUnLikedEvent(product.getId(), product.getBrandId(), member.getId(), LocalDateTime.now()));
    }

    public record Command(MemberInfo memberInfo, Long productId) {
    }
}
