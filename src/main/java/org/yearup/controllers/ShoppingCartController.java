package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;


// only logged in users have access to these actions
@RestController
@RequestMapping("cart")
@CrossOrigin
public class ShoppingCartController
{
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ShoppingCart getCart(Principal principal)
    {
        try
        {

            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            if (user == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");

           return shoppingCartDao.getByUserId(userId);
            // use the shoppingcartDao to get all items in the cart and return the cart
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    //add a product to the cart
    //https://localhost:8080/cart/products/15
    @PostMapping("/products/{productId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart addProductToCart(@PathVariable int productId, Principal principal){
        String username = principal.getName();
        User user = userDao.getByUserName(username);

        Product product = productDao.getById(productId);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");
           if(product == null) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
           }
        return shoppingCartDao.addToCart(user.getId(), productId);
    }

    //update an existing product in the cart
    // https://localhost:8080/cart/products/15
    @PutMapping("/products/{productId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ShoppingCart updateCart (@PathVariable int productId,@RequestBody ShoppingCartItem item, Principal principal) {
        String username = principal.getName();
        User user = userDao.getByUserName(username);

        Product product = productDao.getById(productId);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");
        if(product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
        }
        shoppingCartDao.updateCart(user.getId(), productId, item.getQuantity());
       return shoppingCartDao.getByUserId(user.getId());
    }


    //clear all products from the current users cart
    // https://localhost:8080/cart
    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAllProduct(Principal principal){
        String username = principal.getName();
        User user = userDao.getByUserName(username);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");

        shoppingCartDao.deleteProduct(user.getId());
    }


}
