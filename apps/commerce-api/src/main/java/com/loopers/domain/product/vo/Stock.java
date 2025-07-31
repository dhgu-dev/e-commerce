package com.loopers.domain.product.vo;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Stock implements Serializable {

    public static final Stock ZERO = new Stock(0L);

    @Getter
    private long quantity;

    protected Stock() {
    }

    private Stock(long quantity) {
        if (quantity < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "재고는 음수일 수 없습니다.");
        }
        this.quantity = quantity;
    }

    public static Stock of(long quantity) {
        return new Stock(quantity);
    }

    public Stock deduct(long quantity) {
        if (quantity < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "감소할 재고 수량은 음수일 수 없습니다.");
        }
        if (this.quantity < quantity) {
            throw new CoreException(ErrorType.CONFLICT, "재고가 부족합니다.");
        }
        return new Stock(this.quantity - quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(quantity, stock.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(quantity);
    }
}
