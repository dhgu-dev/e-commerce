package com.loopers.domain.brand;

import com.loopers.domain.product.ProductModel;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandModel getDetail(Long brandId) {
        if (brandId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Brand ID cannot be null");
        }

        Optional<BrandModel> brand = brandRepository.find(brandId);
        if (brand.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND, "Brand not found with ID: " + brandId);
        }

        return brand.get();
    }

    public List<BrandModel> getBrands(Set<Long> brandIds) {
        if (brandIds == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Brand IDs cannot be null");
        }

        if (brandIds.isEmpty()) {
            return Collections.emptyList();
        }

        return brandRepository.findAll(brandIds);
    }

    public List<ProductModel> getRepresentativeProducts(BrandModel brandModel, List<ProductModel> products, long limit) {
        return products.stream()
            .filter(product -> product.getBrandId().equals(brandModel.getId()))
            .sorted(Comparator.comparingLong(ProductModel::getLikeCount).reversed())
            .limit(limit)
            .toList();
    }
}
