package org.yearup.data;

import org.yearup.models.OrderItemLine;

public interface OrderLineItemDao {
    void updateOrderLineItem(int order, OrderItemLine orderItemLine, int product);
}
