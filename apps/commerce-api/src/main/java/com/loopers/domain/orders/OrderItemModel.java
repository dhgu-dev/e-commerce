package com.loopers.domain.orders;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.orders.vo.ProductSnapshot;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemModel extends BaseEntity {

    @ManyToOne()
    @JoinColumn(name = "orders_id")
    private OrdersModel orders;

    @Embedded
    private ProductSnapshot productSnapshot;

    private Long quantity;

    public OrderItemModel(OrdersModel orders, ProductSnapshot productSnapshot, Long quantity) {
        if (orders == null || productSnapshot == null || quantity == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Orders, ProductSnapshot, and Quantity cannot be null.");
        }
        this.orders = orders;
        this.productSnapshot = productSnapshot;
        this.quantity = quantity;
    }

    void setOrders(OrdersModel orders) {
        this.orders = orders;
    }
}
