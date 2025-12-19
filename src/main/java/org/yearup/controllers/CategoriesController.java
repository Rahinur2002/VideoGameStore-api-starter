package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;


@RestController
@RequestMapping("categories")
@CrossOrigin
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;

    //injecting the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    //appropriate annotation for a get action
    @GetMapping("")
    @PreAuthorize("permitAll()")
    public List<Category> getAll()
    {
        //return all categories
        return categoryDao.getAllCategories();
    }


    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int id)
    {
        // get the category by id
        Category category = categoryDao.getById(id);
        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        return category;
    }


    @GetMapping("{categoryId}/products")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        // get a list of product by categoryId
        Category category = categoryDao.getById(categoryId);
        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product by category id not found");
        }
        return productDao.listByCategoryId(categoryId);
    }


    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Category addCategory(@RequestBody Category category)
    {
        try
        {
            return categoryDao.create(category);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }


    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        // update the category by id
            Category existing = categoryDao.getById(id);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid category id");
            }
            try {
                categoryDao.update(id, category);
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops...our bad.");
        }
    }


    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int id)
    {
        // delete the category by id
        Category existing = categoryDao.getById(id);

        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid category id");
        }
        try
        {
            categoryDao.delete(id);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
