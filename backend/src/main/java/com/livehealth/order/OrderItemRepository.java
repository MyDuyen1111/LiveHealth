package com.livehealth.order;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class OrderItemRepository extends BaseRepository<OrderItem, UUID> {

    public OrderItemRepository() {
        super(OrderItem.class);
    }
}
