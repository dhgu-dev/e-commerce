package com.loopers.support;

import com.querydsl.core.types.dsl.BooleanExpression;

public interface Specification<T> {
    BooleanExpression isSatisfiedBy(T candidate);
}
