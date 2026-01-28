package com.example.opensearch.controller;

import com.example.opensearch.model.Product;
import com.example.opensearch.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductSearchService searchService;

    /**
     * Index a single product
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> indexProduct(@RequestBody Product product) {
        try {
            String id = searchService.indexProduct(product);
            Map<String, String> response = new HashMap<>();
            response.put("id", id);
            response.put("message", "Product indexed successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            log.error("Error indexing product: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Bulk index multiple products
     */
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> indexProducts(@RequestBody List<Product> products) {
        try {
            List<String> ids = searchService.indexProducts(products);
            Map<String, Object> response = new HashMap<>();
            response.put("indexed", ids.size());
            response.put("ids", ids);
            response.put("message", "Products indexed successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            log.error("Error indexing products: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search products by query text
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String q) {
        try {
            List<Product> products = searchService.searchProducts(q);
            return ResponseEntity.ok(products);
        } catch (IOException e) {
            log.error("Error searching products: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search products by specific field
     */
    @GetMapping("/search/field")
    public ResponseEntity<List<Product>> searchByField(
            @RequestParam String field, 
            @RequestParam String value) {
        try {
            List<Product> products = searchService.searchByField(field, value);
            return ResponseEntity.ok(products);
        } catch (IOException e) {
            log.error("Error searching by field: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        try {
            Product product = searchService.getProductById(id);
            if (product != null) {
                return ResponseEntity.ok(product);
            }
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            log.error("Error getting product: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = searchService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (IOException e) {
            log.error("Error getting products: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete product by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable String id) {
        try {
            boolean deleted = searchService.deleteProduct(id);
            Map<String, String> response = new HashMap<>();
            if (deleted) {
                response.put("message", "Product deleted successfully");
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            log.error("Error deleting product: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
