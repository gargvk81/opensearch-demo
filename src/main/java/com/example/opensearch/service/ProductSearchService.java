package com.example.opensearch.service;

import com.example.opensearch.model.Product;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductSearchService {

    private static final Logger log = LoggerFactory.getLogger(ProductSearchService.class);
    private final OpenSearchClient client;
    private static final String INDEX_NAME = "products";

    public ProductSearchService(OpenSearchClient client) {
        this.client = client;
    }

    @PostConstruct
    public void init() {
        try {
            createIndexIfNotExists();
        } catch (IOException e) {
            log.error("Error initializing index: ", e);
        }
    }

    private void createIndexIfNotExists() throws IOException {
        ExistsRequest existsRequest = ExistsRequest.of(e -> e.index(INDEX_NAME));
        boolean exists = client.indices().exists(existsRequest).value();
        
        if (!exists) {
            CreateIndexRequest createIndexRequest = CreateIndexRequest.of(c -> c
                .index(INDEX_NAME)
            );
            client.indices().create(createIndexRequest);
            log.info("Index '{}' created successfully", INDEX_NAME);
        } else {
            log.info("Index '{}' already exists", INDEX_NAME);
        }
    }

    /**
     * Index a single product document
     */
    public String indexProduct(Product product) throws IOException {
        IndexRequest<Product> request = IndexRequest.of(i -> i
            .index(INDEX_NAME)
            .id(product.getId())
            .document(product)
        );
        
        IndexResponse response = client.index(request);
        log.info("Product indexed with ID: {}", response.id());
        return response.id();
    }

    /**
     * Bulk index multiple products
     */
    public List<String> indexProducts(List<Product> products) throws IOException {
        List<String> indexedIds = new ArrayList<>();
        
        for (Product product : products) {
            String id = indexProduct(product);
            indexedIds.add(id);
        }
        
        return indexedIds;
    }

    /**
     * Search products by query string (searches across multiple fields)
     */
    public List<Product> searchProducts(String queryText) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .multiMatch(m -> m
                    .query(queryText)
                    .fields("name", "description", "category", "brand")
                )
            )
        );
        
        SearchResponse<Product> response = client.search(searchRequest, Product.class);
        
        return response.hits().hits().stream()
            .map(Hit::source)
            .collect(Collectors.toList());
    }

    /**
     * Search products by specific field
     */
    public List<Product> searchByField(String field, String value) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .match(m -> m
                    .field(field)
                    .query(fv -> fv.stringValue(value))
                )
            )
        );
        
        SearchResponse<Product> response = client.search(searchRequest, Product.class);
        
        return response.hits().hits().stream()
            .map(Hit::source)
            .collect(Collectors.toList());
    }

    /**
     * Get product by ID
     */
    public Product getProductById(String id) throws IOException {
        GetRequest getRequest = GetRequest.of(g -> g
            .index(INDEX_NAME)
            .id(id)
        );
        
        GetResponse<Product> response = client.get(getRequest, Product.class);
        
        if (response.found()) {
            return response.source();
        }
        return null;
    }

    /**
     * Delete product by ID
     */
    public boolean deleteProduct(String id) throws IOException {
        DeleteRequest deleteRequest = DeleteRequest.of(d -> d
            .index(INDEX_NAME)
            .id(id)
        );
        
        DeleteResponse response = client.delete(deleteRequest);
        log.info("Product deleted: {}", response.result());
        return response.result().name().equals("Deleted");
    }

    /**
     * Get all products
     */
    public List<Product> getAllProducts() throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index(INDEX_NAME)
            .query(q -> q.matchAll(m -> m))
            .size(100)
        );
        
        SearchResponse<Product> response = client.search(searchRequest, Product.class);
        
        return response.hits().hits().stream()
            .map(Hit::source)
            .collect(Collectors.toList());
    }
}
