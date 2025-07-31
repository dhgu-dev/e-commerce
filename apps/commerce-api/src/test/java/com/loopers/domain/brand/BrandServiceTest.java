package com.loopers.domain.brand;

import com.loopers.domain.product.ProductModel;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    void getDetail_정상조회() {
        BrandModel brand = mock(BrandModel.class);
        when(brandRepository.find(1L)).thenReturn(Optional.of(brand));

        BrandModel result = brandService.getDetail(1L);

        assertEquals(brand, result);
    }

    @Test
    void getDetail_존재하지않으면_예외() {
        when(brandRepository.find(2L)).thenReturn(Optional.empty());

        CoreException ex = assertThrows(CoreException.class, () -> brandService.getDetail(2L));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
    }

    @Test
    void getDetail_null이면_예외() {
        CoreException ex = assertThrows(CoreException.class, () -> brandService.getDetail(null));
        assertEquals(ErrorType.BAD_REQUEST, ex.getErrorType());
    }

    @Test
    void getBrands_정상조회() {
        Set<Long> ids = Set.of(1L, 2L);
        List<BrandModel> brands = List.of(mock(BrandModel.class), mock(BrandModel.class));
        when(brandRepository.findAll(ids)).thenReturn(brands);

        List<BrandModel> result = brandService.getBrands(ids);

        assertEquals(brands, result);
    }

    @Test
    void getBrands_null이면_예외() {
        CoreException ex = assertThrows(CoreException.class, () -> brandService.getBrands(null));
        assertEquals(ErrorType.BAD_REQUEST, ex.getErrorType());
    }

    @Test
    void getBrands_빈셋이면_빈리스트() {
        List<BrandModel> result = brandService.getBrands(Collections.emptySet());
        assertTrue(result.isEmpty());
    }

    @Test
    void getRepresentativeProducts_정상동작() {
        BrandModel brand = mock(BrandModel.class);
        when(brand.getId()).thenReturn(1L);

        ProductModel p1 = mock(ProductModel.class);
        ProductModel p2 = mock(ProductModel.class);
        ProductModel p3 = mock(ProductModel.class);

        when(p1.getBrandId()).thenReturn(1L);
        when(p2.getBrandId()).thenReturn(1L);
        when(p3.getBrandId()).thenReturn(2L);

        when(p1.getLikeCount()).thenReturn(5L);
        when(p2.getLikeCount()).thenReturn(10L);

        List<ProductModel> products = List.of(p1, p2, p3);

        List<ProductModel> result = brandService.getRepresentativeProducts(brand, products, 1);

        assertEquals(1, result.size());
        assertEquals(p2, result.get(0)); // likeCount 높은 순
    }
}
