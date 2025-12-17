package org.yearup.data;

import org.yearup.models.OrderItemLine;

public interface OrderLineItemDao {
    OrderItemLine createOrderLineItem(OrderItemLine orderItemLine);
}
