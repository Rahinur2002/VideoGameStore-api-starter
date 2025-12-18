package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    @Autowired
    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Order createOrder(int userId, Order order) {
        String createOrderSql = "INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount) " +
                "VALUES (?, NOW(), ?, ?, ?, ?, ?)";


        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(createOrderSql, PreparedStatement.RETURN_GENERATED_KEYS)){
            statement.setInt(1, userId);
            statement.setString(2, order.getAddress());
            statement.setString(3, order.getCity());
            statement.setString(4, order.getState());
            statement.setString(5, order.getZip());
            statement.setBigDecimal(6, order.getShipping_amount());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    order.setOrderid(orderId);

                    return order;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
