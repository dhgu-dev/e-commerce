package com.loopers.application.like.usecase.query;

import com.loopers.application.member.MemberInfo;
import com.loopers.application.product.dto.ProductInfo;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberService;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryMemberLikedProductsUseCase {

    private final MemberService memberService;
    private final ProductService productService;
    private final BrandService brandService;
    private final LikeService likeService;

    @Transactional(readOnly = true)
    public Result execute(Query query) {
        if (query == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Query cannot be null");
        }

        MemberModel member = memberService.getMember(query.memberInfo().userId());

        Set<Long> likedProductIds = likeService.getLikes(member, query.pageRequest()).stream().map(LikeModel::getProductId).collect(Collectors.toSet());
        List<ProductModel> products = productService.getProducts(likedProductIds);

        Map<Long, BrandModel> brandMap = brandService.getBrands(
                products.stream().map(ProductModel::getBrandId).collect(Collectors.toSet())
            )
            .stream()
            .collect(Collectors.toMap(BrandModel::getId, brandModel -> brandModel));

        Page<ProductInfo> result = new PageImpl<>(
            products.stream()
                .map(product -> {
                    BrandModel brand = brandMap.get(product.getBrandId());
                    return ProductInfo.from(product, brand);
                })
                .collect(Collectors.toList()),
            query.pageRequest(),
            likeService.countLikedProducts(member)
        );

        return new Result(query.memberInfo(), result);
    }

    public record Result(MemberInfo member, Page<ProductInfo> products) {
    }

    public record Query(MemberInfo memberInfo, PageRequest pageRequest) {
        public Query {
            if (memberInfo == null || pageRequest == null) {
                throw new CoreException(ErrorType.BAD_REQUEST, "MemberInfo and PageRequest cannot be null");
            }
        }
    }
}
