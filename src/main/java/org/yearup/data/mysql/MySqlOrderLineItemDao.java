package org.yearup.data.mysql;

import org.yearup.data.OrderLineItemDao;
import org.yearup.models.OrderItemLine;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySqlOrderLineItemDao extends MySqlDaoBase implements OrderLineItemDao {


    public MySqlOrderLineItemDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void updateOrderLineItem(int order, OrderItemLine orderItemLine, int product) {
        String sql = "INSERT INTO order_line_items " +
                "(order_id, product_id, sales_price, quantity, discount) " +
                "VALUES (?, ?, ?, ?, ?)";
        ShoppingCartItem item = new ShoppingCartItem();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, order);
            statement.setInt(2, product);
            statement.setBigDecimal(3, orderItemLine.getSales_price());
            statement.setInt(4, item.getQuantity());
            statement.setBigDecimal(5, item.getDiscountPercent());



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
