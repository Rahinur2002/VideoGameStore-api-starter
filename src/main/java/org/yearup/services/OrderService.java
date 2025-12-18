package org.yearup.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yearup.data.*;
import org.yearup.models.*;

import java.math.BigDecimal;

@Service
public class OrderService {
    private final UserDao userDao;
    private final ProfileDao profileDao;
    private final ShoppingCartDao shoppingCartDao;
    private final OrderDao orderDao;
    private final OrderLineItemDao orderLineItemDao;

    @Autowired
    public OrderService(UserDao userDao, ProfileDao profileDao, ShoppingCartDao shoppingCartDao, OrderDao orderDao, OrderLineItemDao orderLineItemDao) {
        this.userDao = userDao;
        this.profileDao = profileDao;
        this.shoppingCartDao = shoppingCartDao;
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
    }

    public Order checkout(String username){
        User user = userDao.getByUserName(username);
        if (user == null) return null;
        int userId = user.getId();

        Profile profile = profileDao.getByUserId(userId);
        if (profile == null) return null;

        ShoppingCart cart = shoppingCartDao.getByUserId(userId);
        if (cart.getItems().isEmpty()) return null;

        Order order = new Order();
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());

        if (order.getShipping_amount() == null)
            order.setShipping_amount(BigDecimal.ZERO);

        Order orderCreated = orderDao.createOrder(userId, order);
        if(orderCreated == null) return null;

        int orderid = orderCreated.getOrder_id();

        for (ShoppingCartItem cartItem : cart.getItems().values()) {
            OrderItemLine orderItemLine = new OrderItemLine(
                    0,
                    orderid,
                    cartItem.getProductId(),
                    cartItem.getProduct().getPrice(),
                    cartItem.getQuantity(),
                    cartItem.getDiscountPercent()
            );

            int productId = cartItem.getProductId();

            orderLineItemDao.createOrderLineItem(orderItemLine);
        }

        shoppingCartDao.deleteProduct(userId);
        return orderCreated;
    }

}
