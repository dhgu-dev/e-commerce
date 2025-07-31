package com.loopers.domain.orders.vo;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
public class Price implements Serializable {

    public static final Price ZERO = new Price(BigDecimal.ZERO);

    @Getter
    private BigDecimal amount;

    protected Price() {
    }

    private Price(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "가격은 null 이거나 음수일 수 없습니다.");
        }
        this.amount = amount;
    }

    public static Price of(BigDecimal amount) {
        return new Price(amount);
    }

    public Price add(Price other) {
        if (other == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "추가할 가격은 null 일 수 없습니다.");
        }
        return new Price(this.amount.add(other.amount));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return amount.equals(price.amount);
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }
}
