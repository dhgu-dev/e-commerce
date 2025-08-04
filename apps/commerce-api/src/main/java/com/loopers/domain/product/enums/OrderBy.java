package com.loopers.domain.product.enums;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public enum OrderBy {
    LATEST("latest"),
    PRICE("price"),
    LIKES("likes");

    private final String property;

    OrderBy(String value) {
        this.property = value;
    }

    public static OrderBy fromValue(String value) {
        for (OrderBy orderBy : OrderBy.values()) {
            if (orderBy.property.equalsIgnoreCase(value)) {
                return orderBy;
            }
        }
        throw new CoreException(ErrorType.BAD_REQUEST, "Invalid property");
    }

    public String getProperty() {
        return property;
    }
}
