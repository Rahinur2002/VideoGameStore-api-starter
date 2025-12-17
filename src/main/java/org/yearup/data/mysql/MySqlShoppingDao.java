package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Category;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingDao extends MySqlDaoBase implements ShoppingCartDao {

    @Autowired
    public MySqlShoppingDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        String sql = "SELECT s.product_id, s.quantity, " +
                "p.name, p.price, p.category_id, p.description, p.subcategory, p.stock, p.featured, p.image_url " +
                "FROM shopping_cart s " +
                "JOIN products p ON p.product_id = s.product_id " +
                "WHERE s.user_id = ?";
        ShoppingCart cart = new ShoppingCart();

        try (Connection connection = dataSource.getConnection();
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
                    cart.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cart;
    }
}

