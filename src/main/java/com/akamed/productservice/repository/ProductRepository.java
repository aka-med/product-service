package com.akamed.productservice.repository;

import com.akamed.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findProductByCategory(String name);


    List<Product> findProductByPriceLessThanEqual(Double maxprice);


    List<Product> findProductByNameContainingIgnoreCase(String name);
}
