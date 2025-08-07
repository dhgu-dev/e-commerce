package com.loopers.domain.orders;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.orders.vo.Price;
import com.loopers.domain.orders.vo.ProductSnapshot;
import com.loopers.domain.product.ProductModel;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrdersModel order(MemberModel member, List<Pair<ProductModel, Long>> products, Long couponId) {
        if (member == null || products == null || products.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Member and Products cannot be null or empty.");
        }

        Price totalPrice = Price.of(products.stream()
            .map(pair -> pair.getFirst().getPrice().multiply(pair.getSecond()).getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add));

        OrdersModel order = new OrdersModel(member.getId(), totalPrice, couponId);
        OrdersModel savedOrder = orderRepository.save(order);

        for (Pair<ProductModel, Long> pair : products) {
            ProductModel product = pair.getFirst();
            Long quantity = pair.getSecond();

            if (quantity <= 0) {
                throw new CoreException(ErrorType.BAD_REQUEST, "Product and quantity must be valid.");
            }

            OrderItemModel item = new OrderItemModel(
                savedOrder,
                ProductSnapshot.of(product.getId(), product.getName(), Price.of(product.getPrice().getAmount())),
                quantity
            );
            savedOrder.addItem(item);
        }

        return savedOrder;
    }

    public List<OrdersModel> getOrders(MemberModel member) {
        if (member == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Member cannot be null.");
        }
        return orderRepository.search(member.getId());
    }

    public OrdersModel getDetail(Long orderId) {
        if (orderId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Order ID cannot be null.");
        }

        return orderRepository.find(orderId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Order not found."));
    }
}
