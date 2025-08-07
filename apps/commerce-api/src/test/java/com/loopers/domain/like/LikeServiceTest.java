package com.loopers.domain.like;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.product.ProductModel;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    void like_정상동작() {
        MemberModel member = mock(MemberModel.class);
        ProductModel product = mock(ProductModel.class);
        when(member.getId()).thenReturn(1L);
        when(product.getId()).thenReturn(2L);
        when(likeRepository.find(1L, 2L)).thenReturn(Optional.empty());

        likeService.like(member, product);

        verify(likeRepository).save(any(LikeModel.class));
    }

    @Test
    void like_이미_좋아요_있으면_save_호출안함() {
        MemberModel member = mock(MemberModel.class);
        ProductModel product = mock(ProductModel.class);
        when(member.getId()).thenReturn(1L);
        when(product.getId()).thenReturn(2L);
        when(likeRepository.find(1L, 2L)).thenReturn(Optional.of(mock(LikeModel.class)));

        likeService.like(member, product);

        verify(likeRepository, never()).save(any());
    }

    @Test
    void like_null_입력시_예외() {
        assertThrows(CoreException.class, () -> likeService.like(null, null));
    }

    @Test
    void unlike_정상동작() {
        MemberModel member = mock(MemberModel.class);
        ProductModel product = mock(ProductModel.class);
        LikeModel like = mock(LikeModel.class);
        when(member.getId()).thenReturn(1L);
        when(product.getId()).thenReturn(2L);
        when(likeRepository.findWithLock(1L, 2L)).thenReturn(Optional.of(like));

        likeService.unlike(member, product);

        verify(likeRepository).delete(like);
    }

    @Test
    void unlike_좋아요없으면_delete_호출안함() {
        MemberModel member = mock(MemberModel.class);
        ProductModel product = mock(ProductModel.class);
        when(likeRepository.findWithLock(any(), any())).thenReturn(Optional.empty());

        likeService.unlike(member, product);

        verify(likeRepository, never()).delete(any());
    }

    @Test
    void unlike_null_입력시_예외() {
        assertThrows(CoreException.class, () -> likeService.unlike(null, null));
    }

    @Test
    void getLikeCount_정상동작() {
        ProductModel product = mock(ProductModel.class);
        when(product.getId()).thenReturn(2L);
        when(likeRepository.getProductLikeCount(2L)).thenReturn(5L);

        long count = likeService.getLikeCount(product);

        assertEquals(5L, count);
    }

    @Test
    void getLikeCount_null_입력시_예외() {
        assertThrows(CoreException.class, () -> likeService.getLikeCount(null));
    }

    @Test
    void getLikes_정상동작() {
        MemberModel member = mock(MemberModel.class);
        Pageable pageable = mock(Pageable.class);
        List<LikeModel> likes = List.of(mock(LikeModel.class));
        when(likeRepository.search(member, pageable)).thenReturn(likes);

        List<LikeModel> result = likeService.getLikes(member, pageable);

        assertEquals(likes, result);
    }

    @Test
    void getLikes_null_입력시_예외() {
        assertThrows(CoreException.class, () -> likeService.getLikes(null, null));
    }

    @Test
    void countLikedProducts_정상동작() {
        MemberModel member = mock(MemberModel.class);
        when(member.getId()).thenReturn(1L);
        when(likeRepository.countMemberLikedProducts(1L)).thenReturn(3L);

        long count = likeService.countLikedProducts(member);

        assertEquals(3L, count);
    }

    @Test
    void countLikedProducts_null_입력시_예외() {
        assertThrows(CoreException.class, () -> likeService.countLikedProducts(null));
    }
}
