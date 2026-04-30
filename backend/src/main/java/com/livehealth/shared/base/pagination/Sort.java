package com.livehealth.shared.base.pagination;

import java.util.List;

public class Sort {
    public enum Direction {
        ASC, DESC
    }

    public static class Order {
        private final Direction direction;
        private final String property;

        public Order(Direction direction, String property) {
            this.direction = direction;
            this.property = property;
        }

        public Direction getDirection() { return direction; }
        public String getProperty() { return property; }
    }

    private final List<Order> orders;

    private Sort(List<Order> orders) {
        this.orders = orders;
    }

    public static Sort unsorted() {
        return new Sort(List.of());
    }

    public static Sort by(Direction direction, String... properties) {
        java.util.List<Order> list = new java.util.ArrayList<>();
        for (String property : properties) {
            list.add(new Order(direction, property));
        }
        return new Sort(list);
    }

    public boolean isSorted() {
        return !orders.isEmpty();
    }

    public List<Order> getOrders() {
        return orders;
    }
}
