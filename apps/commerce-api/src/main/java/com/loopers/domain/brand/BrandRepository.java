package com.loopers.domain.brand;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BrandRepository {
    Optional<BrandModel> find(Long brandId);

    List<BrandModel> findAll(Set<Long> brandIds);
}
