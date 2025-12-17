package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.OrderLineItemDao;
import org.yearup.models.OrderItemLine;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlOrderLineItemDao extends MySqlDaoBase implements OrderLineItemDao {


    @Autowired
    public MySqlOrderLineItemDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public OrderItemLine createOrderLineItem(int order, OrderItemLine orderItemLine, int product) {
        String sql = "INSERT INTO order_line_items " +
                "(order_id, product_id, sales_price, quantity, discount) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            statement.setInt(1, order);
            statement.setInt(2, product);
            statement.setBigDecimal(3, orderItemLine.getSales_price());
            statement.setInt(4, orderItemLine.getQuantity());
            statement.setBigDecimal(5, orderItemLine.getDiscount());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        int orderItemLineId = generatedKeys.getInt(1);
                        orderItemLine.setOrder_line_item_id(orderItemLineId);

                        return orderItemLine;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
