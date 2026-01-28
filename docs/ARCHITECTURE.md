# Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER / CLIENT                            │
│                    (curl, Postman, Browser)                      │
└────────────────────────┬────────────────────────────────────────┘
                         │ HTTP Requests
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│              SPRING BOOT APPLICATION (Port 8080)                 │
│                                                                   │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Controller Layer (ProductController.java)                 │  │
│  │  • POST   /api/products          - Index single product    │  │
│  │  • POST   /api/products/bulk     - Bulk index products     │  │
│  │  • GET    /api/products/search   - Search products         │  │
│  │  • GET    /api/products          - Get all products        │  │
│  │  • GET    /api/products/{id}     - Get product by ID       │  │
│  │  • DELETE /api/products/{id}     - Delete product          │  │
│  └───────────────────┬───────────────────────────────────────┘  │
│                      │                                            │
│  ┌───────────────────▼───────────────────────────────────────┐  │
│  │  Service Layer (ProductSearchService.java)                 │  │
│  │  • indexProduct()       - Index single document            │  │
│  │  • indexProducts()      - Bulk indexing                    │  │
│  │  • searchProducts()     - Multi-field search               │  │
│  │  • searchByField()      - Field-specific search            │  │
│  │  • getAllProducts()     - Match all query                  │  │
│  │  • getProductById()     - Get by ID                        │  │
│  │  • deleteProduct()      - Delete document                  │  │
│  └───────────────────┬───────────────────────────────────────┘  │
│                      │                                            │
│  ┌───────────────────▼───────────────────────────────────────┐  │
│  │  OpenSearch Client (OpenSearchConfig.java)                 │  │
│  │  • OpenSearchClient     - Main client instance             │  │
│  │  • RestClientTransport  - HTTP transport layer             │  │
│  │  • JacksonJsonpMapper   - JSON serialization               │  │
│  │  • Credentials Provider - Authentication                   │  │
│  └───────────────────┬───────────────────────────────────────┘  │
│                      │                                            │
│  ┌───────────────────▼───────────────────────────────────────┐  │
│  │  Model Layer (Product.java)                                │  │
│  │  • id: String                                               │  │
│  │  • name: String                                             │  │
│  │  • description: String                                      │  │
│  │  • price: Double                                            │  │
│  │  • category: String                                         │  │
│  │  • brand: String                                            │  │
│  └───────────────────┬───────────────────────────────────────┘  │
└────────────────────────┼────────────────────────────────────────┘
                         │ HTTP/REST API Calls
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│              OPENSEARCH SERVER (Port 9200)                       │
│                                                                   │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  REST API Layer                                            │  │
│  │  • Receives HTTP requests                                  │  │
│  │  • Routes to appropriate handlers                          │  │
│  └───────────────────┬───────────────────────────────────────┘  │
│                      │                                            │
│  ┌───────────────────▼───────────────────────────────────────┐  │
│  │  Text Analysis Engine                                      │  │
│  │  • Tokenization    - Split text into words                 │  │
│  │  • Lowercase       - Normalize case                        │  │
│  │  • Stemming        - Reduce to root form                   │  │
│  │  • Stop words      - Remove common words                   │  │
│  └───────────────────┬───────────────────────────────────────┘  │
│                      │                                            │
│  ┌───────────────────▼───────────────────────────────────────┐  │
│  │  Inverted Index                                            │  │
│  │  ┌─────────────┬─────────────────────────────────────┐    │  │
│  │  │ Term        │ Document IDs                        │    │  │
│  │  ├─────────────┼─────────────────────────────────────┤    │  │
│  │  │ laptop      │ [1, 5, 12]                          │    │  │
│  │  │ wireless    │ [2, 8]                              │    │  │
│  │  │ high        │ [1, 3, 5]                           │    │  │
│  │  │ performance │ [1, 5]                              │    │  │
│  │  └─────────────┴─────────────────────────────────────┘    │  │
│  └───────────────────┬───────────────────────────────────────┘  │
│                      │                                            │
│  ┌───────────────────▼───────────────────────────────────────┐  │
│  │  Scoring Engine (BM25)                                     │  │
│  │  • Calculate relevance scores                              │  │
│  │  • Rank results by score                                   │  │
│  │  • Return top matching documents                           │  │
│  └───────────────────┬───────────────────────────────────────┘  │
│                      │                                            │
│  ┌───────────────────▼───────────────────────────────────────┐  │
│  │  Document Store                                            │  │
│  │  • Original JSON documents                                 │  │
│  │  • Indexed by document ID                                  │  │
│  │  • Fast retrieval                                          │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow

### 1. Indexing Flow (Write Path)

```
User sends POST request with Product JSON
    ↓
Controller receives HTTP request
    ↓
Service layer validates and processes
    ↓
OpenSearch Client serializes to JSON
    ↓
HTTP request sent to OpenSearch Server
    ↓
OpenSearch analyzes text fields:
  - "High Performance Laptop" → ["high", "perform", "laptop"]
    ↓
Creates/updates inverted index:
  - "high" → [doc1]
  - "perform" → [doc1]
  - "laptop" → [doc1]
    ↓
Stores original document in document store
    ↓
Returns success response with document ID
    ↓
Response bubbles back through layers
    ↓
User receives confirmation
```

### 2. Search Flow (Read Path)

```
User sends GET request with query "wireless laptop"
    ↓
Controller receives HTTP request
    ↓
Service layer constructs OpenSearch Query DSL:
  {
    "multi_match": {
      "query": "wireless laptop",
      "fields": ["name", "description", "category", "brand"]
    }
  }
    ↓
OpenSearch Client sends query to server
    ↓
OpenSearch analyzes query:
  - "wireless laptop" → ["wireless", "laptop"]
    ↓
Searches inverted index:
  - "wireless" → [doc2, doc8]
  - "laptop" → [doc1, doc5, doc12]
  - Intersection/Union based on query type
    ↓
Scores documents using BM25:
  - doc1: 8.5 (contains "laptop", high relevance)
  - doc2: 7.2 (contains "wireless")
  - doc8: 6.8 (contains "wireless")
    ↓
Retrieves full documents from document store
    ↓
Returns sorted results [doc1, doc2, doc8]
    ↓
OpenSearch Client deserializes JSON to Product objects
    ↓
Service layer processes results
    ↓
Controller formats HTTP response
    ↓
User receives List<Product> sorted by relevance
```

## Component Responsibilities

### Spring Boot Layer

| Component | Responsibility | Key Classes |
|-----------|----------------|-------------|
| Controller | HTTP endpoint handling, request/response mapping | `ProductController` |
| Service | Business logic, OpenSearch operations | `ProductSearchService` |
| Config | OpenSearch client configuration, connection management | `OpenSearchConfig` |
| Model | Data structures, JSON mapping | `Product` |

### OpenSearch Layer

| Component | Responsibility | Technology |
|-----------|----------------|------------|
| REST API | Accept HTTP requests | Netty HTTP Server |
| Analyzer | Text processing | Standard Analyzer |
| Index | Store inverted index | Lucene |
| Storage | Document persistence | Lucene Segments |
| Scoring | Relevance ranking | BM25 Algorithm |

## Technology Stack

### Application Layer
- **Java 17**: Programming language
- **Spring Boot 3.2.1**: Application framework
- **Maven**: Build tool and dependency management
- **Lombok**: Reduce boilerplate code
- **Jackson**: JSON serialization/deserialization

### Client Layer
- **OpenSearch Java Client 2.11.1**: Type-safe OpenSearch API
- **OpenSearch REST Client**: Low-level HTTP client
- **Apache HttpClient**: HTTP communication

### Search Engine Layer
- **OpenSearch 2.11.1**: Search and analytics engine
- **Lucene**: Underlying search library
- **Docker**: Containerization

## Key Design Patterns

### 1. Dependency Injection
```java
@Service
@RequiredArgsConstructor  // Lombok generates constructor
public class ProductSearchService {
    private final OpenSearchClient client;  // Injected by Spring
}
```

### 2. Builder Pattern (Fluent API)
```java
SearchRequest searchRequest = SearchRequest.of(s -> s
    .index(INDEX_NAME)
    .query(q -> q.multiMatch(m -> m
        .query(queryText)
        .fields("name", "description")
    ))
);
```

### 3. Repository Pattern
Service layer acts as repository, abstracting OpenSearch operations.

### 4. Configuration Management
Externalized configuration in `application.yml`, injected via `@Value`.

## Scalability Considerations

### Current Setup (Single Node)
- Suitable for development and small datasets
- No redundancy or failover
- Limited to single machine resources

### Production Setup (Cluster)
- Multiple OpenSearch nodes
- Data replication for redundancy
- Horizontal scaling with shards
- Load balancing
- High availability

### Future Enhancements
1. **Connection Pooling**: Reuse HTTP connections
2. **Bulk Indexing**: Batch multiple documents
3. **Async Operations**: Non-blocking I/O
4. **Caching**: Redis for frequent queries
5. **Rate Limiting**: Protect from abuse
6. **Monitoring**: Metrics and logging

## Security Components

### Authentication
- Basic Auth (username/password)
- Configured in `application.yml` and `docker-compose.yml`
- Production: Use SSL/TLS, OAuth, or SAML

### Authorization
- Currently: Admin user with full access
- Production: Role-based access control (RBAC)

### Data Security
- Network: Container network isolation
- Transport: HTTP (should be HTTPS in production)
- At-rest: Encryption optional (configure in OpenSearch)

## Monitoring & Observability

### Application Monitoring
- Spring Boot Actuator (can be added)
- Application logs (Logback)
- Custom metrics

### OpenSearch Monitoring
- OpenSearch Dashboards UI
- Index stats API: `GET /_cat/indices`
- Node stats API: `GET /_cat/nodes`
- Cluster health: `GET /_cluster/health`

---

**This architecture provides a solid foundation for building production-grade search applications!** 🚀
