package com.akamed.productservice.service;


import com.akamed.productservice.model.Product;
import com.akamed.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }


    public List<Product> getProductByCategory(String categoryName) {
        return productRepository.findProductByCategory(categoryName);
    }

    public List<Product> getProductsByPriceLessThanEqual(Double price) {
        return productRepository.findProductByPriceLessThanEqual(price);
    }


    public List<Product> getProductsByProductName(String name) {
        return productRepository.findProductByNameContainingIgnoreCase(name);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }
}
