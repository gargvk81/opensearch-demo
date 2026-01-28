# Spring Boot + OpenSearch Demo Application

A Hello World Spring Boot application demonstrating OpenSearch integration for full-text search capabilities.

## Components Architecture

### 1. **OpenSearch Server**
   - Search and analytics engine (similar to Elasticsearch)
   - Runs in Docker container
   - Accessible at `http://localhost:9200`

### 2. **OpenSearch Java Client**
   - Official client library for Java applications
   - Provides REST client for communication with OpenSearch
   - Handles JSON serialization/deserialization

### 3. **Spring Boot Application**
   - REST API layer exposing search functionality
   - Configuration management
   - Dependency injection

### 4. **Key Application Components**

#### a. Configuration Layer (`OpenSearchConfig`)
   - Creates and configures OpenSearch client
   - Manages connection settings and credentials
   - Provides client bean for dependency injection

#### b. Model Layer (`Product`)
   - POJO representing searchable documents
   - JSON annotations for proper serialization
   - Contains fields: id, name, description, price, category, brand

#### c. Service Layer (`ProductSearchService`)
   - Business logic for search operations
   - Index management (create, check existence)
   - CRUD operations on documents
   - Search functionality (multi-match, field-specific, match-all)

#### d. Controller Layer (`ProductController`)
   - REST endpoints for external access
   - Request/response handling
   - Error management

### 5. **OpenSearch Dashboards** (Optional)
   - Web UI for OpenSearch visualization
   - Query testing interface
   - Index management
   - Accessible at `http://localhost:5601`

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose

## Getting Started

### Step 1: Start OpenSearch

```bash
# Start OpenSearch and OpenSearch Dashboards
docker-compose up -d

# Wait for OpenSearch to be ready (about 30 seconds)
# Check health
curl -XGET "http://localhost:9200" -u admin:Admin@123 -k
```

### Step 2: Build and Run the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### 1. Index a Single Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "id": "1",
    "name": "Laptop",
    "description": "High performance laptop with 16GB RAM",
    "price": 999.99,
    "category": "Electronics",
    "brand": "TechBrand"
  }'
```

### 2. Bulk Index Products

```bash
curl -X POST http://localhost:8080/api/products/bulk \
  -H "Content-Type: application/json" \
  -d '[
    {
      "id": "1",
      "name": "Laptop",
      "description": "High performance laptop with 16GB RAM",
      "price": 999.99,
      "category": "Electronics",
      "brand": "TechBrand"
    },
    {
      "id": "2",
      "name": "Wireless Mouse",
      "description": "Ergonomic wireless mouse with 6 buttons",
      "price": 29.99,
      "category": "Electronics",
      "brand": "TechBrand"
    },
    {
      "id": "3",
      "name": "Coffee Maker",
      "description": "Automatic coffee maker with timer",
      "price": 79.99,
      "category": "Home Appliances",
      "brand": "HomeBrand"
    }
  ]'
```

### 3. Search Products (Multi-field search)

```bash
# Search for "laptop"
curl "http://localhost:8080/api/products/search?q=laptop"

# Search for "wireless"
curl "http://localhost:8080/api/products/search?q=wireless"

# Search for "TechBrand"
curl "http://localhost:8080/api/products/search?q=TechBrand"
```

### 4. Search by Specific Field

```bash
# Search by category
curl "http://localhost:8080/api/products/search/field?field=category&value=Electronics"

# Search by brand
curl "http://localhost:8080/api/products/search/field?field=brand&value=TechBrand"
```

### 5. Get All Products

```bash
curl http://localhost:8080/api/products
```

### 6. Get Product by ID

```bash
curl http://localhost:8080/api/products/1
```

### 7. Delete Product

```bash
curl -X DELETE http://localhost:8080/api/products/1
```

## How OpenSearch Works

### Document Indexing Process
1. Application receives product data via REST API
2. Service layer converts Java object to JSON
3. OpenSearch client sends HTTP request to OpenSearch server
4. OpenSearch indexes the document (analyzes text, creates inverted index)
5. Document is stored and becomes searchable

### Search Process
1. User submits search query via REST API
2. Service layer constructs OpenSearch query DSL
3. Query is sent to OpenSearch server
4. OpenSearch:
   - Analyzes query text
   - Searches inverted index
   - Ranks results by relevance score
   - Returns matching documents
5. Results are deserialized to Java objects
6. Response is returned to user

### Index Structure
- **Inverted Index**: Maps terms to document locations
- **Analyzers**: Process text (tokenization, lowercase, stemming)
- **Scoring**: BM25 algorithm for relevance ranking
- **Shards**: Horizontal scaling (not used in single-node setup)

## Testing with OpenSearch Dashboards

1. Navigate to `http://localhost:5601`
2. Login with `admin` / `Admin@123`
3. Go to Dev Tools
4. Run queries directly:

```json
GET /products/_search
{
  "query": {
    "match": {
      "name": "laptop"
    }
  }
}
```

## Project Structure

```
opensearch-demo/
├── src/
│   └── main/
│       ├── java/com/example/opensearch/
│       │   ├── OpenSearchDemoApplication.java    # Main application
│       │   ├── config/
│       │   │   └── OpenSearchConfig.java         # OpenSearch client config
│       │   ├── model/
│       │   │   └── Product.java                  # Data model
│       │   ├── service/
│       │   │   └── ProductSearchService.java     # Business logic
│       │   └── controller/
│       │       └── ProductController.java        # REST endpoints
│       └── resources/
│           └── application.yml                    # Configuration
├── docs/
│   ├── QUICKSTART.md                              # Quick start guide
│   ├── ARCHITECTURE.md                            # Architecture diagrams
│   ├── GIT-COMMANDS.md                            # Git workflow reference
│   └── GET-STARTED.txt                            # Getting started guide
├── scripts/
│   ├── load-sample-data.sh                        # Load sample data
│   └── test-api.sh                                # API testing script
├── data/
│   └── sample-products.json                       # Sample product data
├── docker-compose.yml                             # OpenSearch setup
├── pom.xml                                        # Maven dependencies
└── README.md                                      # This file
```

## Key Dependencies

- **spring-boot-starter-web**: REST API framework
- **opensearch-java**: Official OpenSearch client
- **opensearch-rest-client**: HTTP client for OpenSearch
- **jackson-databind**: JSON processing
- **lombok**: Reduce boilerplate code

## Troubleshooting

### OpenSearch not starting
```bash
# Check logs
docker logs opensearch

# Ensure enough memory
# On Mac/Windows, allocate at least 4GB to Docker
```

### Connection refused errors
```bash
# Check if OpenSearch is running
docker ps

# Test connection
curl -XGET "http://localhost:9200" -u admin:Admin@123 -k
```

### Application can't connect
- Verify credentials match between application.yml and docker-compose.yml
- Ensure OpenSearch is fully started before running the app

## Advanced Features to Explore

1. **Aggregations**: Group and analyze data
2. **Fuzzy Search**: Handle typos and misspellings
3. **Filters**: Combine search with exact matches
4. **Highlighting**: Show matching text snippets
5. **Pagination**: Handle large result sets
6. **Synonyms**: Expand search terms
7. **Autocomplete**: Suggest search terms
8. **Geospatial Search**: Location-based queries

## Cleanup

```bash
# Stop and remove containers
docker-compose down

# Remove volumes (deletes all indexed data)
docker-compose down -v
```

## License

This is a demo project for learning purposes.
