package com.akamed.productservice.controller;

import com.akamed.productservice.model.Product;
import com.akamed.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {


    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProduct());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(
                        () -> new RuntimeException("Product not found")
                );
        return ResponseEntity.ok(product);
    }


    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String categoryName) {
        return ResponseEntity.ok(productService.getProductByCategory(categoryName));
    }

    @GetMapping("/price/{maxPrice}")
    public ResponseEntity<List<Product>> getProductByMaxPrice(@PathVariable Double maxPrice) {
        return ResponseEntity.ok(productService.getProductsByPriceLessThanEqual(maxPrice));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProduct(@RequestParam String name) {
        return ResponseEntity.ok(productService.getProductsByProductName(name));
    }


    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product newProduct = productService.saveProduct(product);

        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product productUpdate = productService.getProductById(id)
                .orElseThrow(
                        () -> new RuntimeException("Product not found")
                );
        productUpdate.setName(product.getName());
        productUpdate.setDescription(product.getDescription());
        productUpdate.setPrice(product.getPrice());
        productUpdate.setCategory(product.getCategory());

        Product updatedProd = productService.saveProduct(productUpdate);
        return new ResponseEntity<>(updatedProd, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Product productDelete = productService.getProductById(id)
                .orElseThrow(
                        () -> new RuntimeException("Product not found")
                );

        productService.deleteProduct(productDelete);
        return ResponseEntity.ok("Product deleted!");
    }

}

