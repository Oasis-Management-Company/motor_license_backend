package com.app.IVAS.filter;

import com.querydsl.core.types.Order;
import lombok.Data;

import java.util.Optional;

@Data
public class BaseSearchFilter {


    private Integer offset;
    private Integer limit;
    private Order order;
    private String orderColumn;

    public Optional<Integer> getOffset() {
        return Optional.ofNullable(offset);
    }

    public Optional<Integer> getLimit() {
        return Optional.ofNullable(limit);
    }

    public Optional<Order> getOrder() {
        return Optional.ofNullable(order);
    }

    public Optional<String> getOrderColumn() {
        return Optional.ofNullable(orderColumn);
    }
}
