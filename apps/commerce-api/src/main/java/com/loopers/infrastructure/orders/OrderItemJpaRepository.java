package com.loopers.infrastructure.orders;

import com.loopers.domain.orders.OrderItemModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemModel, Long> {

}
