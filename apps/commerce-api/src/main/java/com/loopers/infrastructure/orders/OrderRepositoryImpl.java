package com.loopers.infrastructure.orders;

import com.loopers.domain.orders.OrderItemModel;
import com.loopers.domain.orders.OrderRepository;
import com.loopers.domain.orders.OrdersModel;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.loopers.domain.orders.QOrderItemModel.orderItemModel;
import static com.loopers.domain.orders.QOrdersModel.ordersModel;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public OrdersModel save(OrdersModel order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public List<OrderItemModel> saveAll(Set<OrderItemModel> items) {
        return orderItemJpaRepository.saveAll(items);
    }

    @Override
    public List<OrdersModel> search(Long memberId) {
        return queryFactory
            .selectFrom(ordersModel)
            .distinct()
            .leftJoin(ordersModel.items, orderItemModel).fetchJoin()
            .where(ordersModel.memberId.eq(memberId))
            .fetch();
    }

    @Override
    public Optional<OrdersModel> find(Long orderId) {
        OrdersModel order = queryFactory
            .selectFrom(ordersModel)
            .leftJoin(ordersModel.items, orderItemModel).fetchJoin()
            .where(ordersModel.id.eq(orderId))
            .fetchOne();
        return Optional.ofNullable(order);
    }

    @Override
    public List<OrdersModel> searchByCoupon(Long couponId) {
        return queryFactory
            .selectFrom(ordersModel)
            .distinct()
            .leftJoin(ordersModel.items, orderItemModel).fetchJoin()
            .where(ordersModel.couponId.eq(couponId))
            .fetch();
    }
}
