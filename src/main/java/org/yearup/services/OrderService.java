package org.yearup.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        int userId = user.getId();

        Profile profile = profileDao.getByUserId(userId);
        if (profile == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile is needed to checkout");

        ShoppingCart cart = shoppingCartDao.getByUserId(userId);
        if (cart.getItems().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cart is empty");

        Order order = new Order();
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());

        if (order.getShippingAmount() == null)
            order.setShippingAmount(BigDecimal.ZERO);

        Order orderCreated = orderDao.createOrder(userId, order);
        if(orderCreated == null) return null;

        int orderId = orderCreated.getOrderId();

        //adding cart items in orderItemLine
        for (ShoppingCartItem cartItem : cart.getItems().values()) {
            OrderItemLine orderItemLine = new OrderItemLine(
                    0,
                    orderId,
                    cartItem.getProductId(),
                    cartItem.getProduct().getPrice(),
                    cartItem.getQuantity(),
                    cartItem.getDiscountPercent()
            );

            orderLineItemDao.createOrderLineItem(orderItemLine);
        }

        shoppingCartDao.deleteProduct(userId);
        return orderCreated;
    }


}
