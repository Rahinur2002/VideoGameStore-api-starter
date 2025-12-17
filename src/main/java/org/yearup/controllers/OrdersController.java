package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Order;
import org.yearup.services.OrderService;

import java.security.Principal;

@RestController
@RequestMapping ("orders")
@CrossOrigin
public class OrdersController {

    private OrderService orderService;

    @Autowired
    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void checkout(Principal principal) {
        Order orderCreated = orderService.checkout(principal.getName());

        if (orderCreated == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkout failed");
        }
    }
}
