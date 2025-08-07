package com.loopers.domain.orders;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.orders.enums.OrderStatus;
import com.loopers.domain.orders.vo.Price;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrdersModel extends BaseEntity {

    @Getter
    private Long memberId;

    @Getter
    private Long couponId;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "total_price"))
    @Getter
    private Price totalPrice;

    @Enumerated(EnumType.STRING)
    @Getter
    private OrderStatus status;

    @OneToMany(mappedBy = "orders", fetch = FetchType.LAZY)
    @Getter
    private Set<OrderItemModel> items = new LinkedHashSet<>();

    public OrdersModel(Long memberId, Price totalPrice) {
        this(memberId, totalPrice, null);
    }

    public OrdersModel(Long memberId, Price totalPrice, Long couponId) {
        if (memberId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Member ID cannot be null.");
        }
        if (totalPrice == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Total price cannot be null.");
        }

        this.memberId = memberId;
        this.totalPrice = totalPrice;
        this.status = OrderStatus.NOT_PAID;
        this.couponId = couponId;
    }

    public void addItem(OrderItemModel item) {
        items.add(item);
        item.setOrders(this);
    }
}
