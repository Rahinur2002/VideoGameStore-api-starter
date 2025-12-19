package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        String sql = "SELECT s.product_id, s.quantity, " +
                "p.name, p.price, p.category_id, p.description, p.subcategory, p.stock, p.image_url, p.featured " +
                "FROM shopping_cart s " +
                "JOIN products p ON p.product_id = s.product_id " +
                "WHERE s.user_id = ?";
        //Empty cart
        ShoppingCart cart = new ShoppingCart();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product(
                            resultSet.getInt("product_id"),
                            resultSet.getString("name"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getInt("category_id"),
                            resultSet.getString("description"),
                            resultSet.getString("subcategory"),
                            resultSet.getInt("stock"),
                            resultSet.getBoolean("featured"),
                            resultSet.getString("image_url")
                    );
                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(resultSet.getInt("quantity"));
                    item.setDiscountPercent(BigDecimal.ZERO);
                    //adding to the empty cart
                    cart.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cart;
    }

    @Override
    public ShoppingCart addToCart(int userId, int productId){
        String updateSql = "UPDATE shopping_cart SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, 1)";

        try (Connection connection = getConnection())
        {
            try (PreparedStatement updateStatement = connection.prepareStatement(updateSql))
            {
                updateStatement.setInt(1, userId);
                updateStatement.setInt(2, productId);

                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated == 0)
                {
                    try (PreparedStatement insertStatement= connection.prepareStatement(insertSql))
                    {
                        insertStatement.setInt(1, userId);
                        insertStatement.setInt(2, productId);
                        insertStatement.executeUpdate();
                    }
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return getByUserId(userId);
    }

    @Override
    public void updateCart(int userId, int productId, int quantity) {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, quantity);
            stmt.setInt(2, userId);
            stmt.setInt(3, productId);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProduct(int userId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, userId);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

