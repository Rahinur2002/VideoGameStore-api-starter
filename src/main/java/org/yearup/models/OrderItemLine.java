package org.yearup.models;

import java.math.BigDecimal;

public class OrderItemLine {
    private int order_line_item_id;
    private int order_id;
    private int product_id;
    private BigDecimal sales_price;
    private int quantity;
    private BigDecimal discount;

    public OrderItemLine(int order_line_item_id, int order_id, int product_id, BigDecimal sales_price, int quantity, BigDecimal discount) {
        this.order_line_item_id = order_line_item_id;
        this.order_id = order_id;
        this.product_id = product_id;
        this.sales_price = sales_price;
        this.quantity = quantity;
        this.discount = discount;
    }

    public int getOrder_line_item_id() {
        return order_line_item_id;
    }

    public void setOrder_line_item_id(int order_line_item_id) {
        this.order_line_item_id = order_line_item_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public BigDecimal getSales_price() {
        return sales_price;
    }

    public void setSales_price(BigDecimal sales_price) {
        this.sales_price = sales_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}
