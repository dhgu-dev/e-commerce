package com.loopers.domain.orders;

import java.util.Set;

public interface ProductStockManager {
    void restoreAllStock(Set<OrderItemModel> items);
}
