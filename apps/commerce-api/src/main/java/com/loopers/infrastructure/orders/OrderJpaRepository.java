package com.loopers.infrastructure.orders;

import com.loopers.domain.orders.OrdersModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrdersModel, Long> {
}
