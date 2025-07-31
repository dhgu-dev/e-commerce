package com.loopers.domain.orders;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderRepository {
    OrdersModel save(OrdersModel order);

    List<OrderItemModel> saveAll(Set<OrderItemModel> items);

    List<OrdersModel> search(Long memberId);

    Optional<OrdersModel> find(Long orderId);
}
