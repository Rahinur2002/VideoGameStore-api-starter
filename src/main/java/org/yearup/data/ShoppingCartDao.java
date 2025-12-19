package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    ShoppingCart addToCart(int userId, int productId);
    void updateCart(int userId, int productId, int quantity);
    void deleteProduct(int userId);
}
