# Quick Start Guide

## 🚀 Get Started in 3 Steps

### Step 1: Start OpenSearch
```bash
cd /Users/vgarg/opensearch-demo
docker-compose up -d
```

Wait about 30 seconds for OpenSearch to start up completely.

### Step 2: Build and Run the Application
```bash
mvn clean install
mvn spring-boot:run
```

Wait for the Spring Boot application to start (you'll see "Started OpenSearchDemoApplication").

### Step 3: Test the API
```bash
# Run the automated test script
./test-api.sh

# Or manually test individual endpoints:
# Index a product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"id":"1","name":"Laptop","description":"High performance laptop","price":999.99,"category":"Electronics","brand":"TechBrand"}'

# Search for products
curl "http://localhost:8080/api/products/search?q=laptop"
```

## 📊 Key Components Explained

### 1. **OpenSearch Server** (Port 9200)
- The search engine that indexes and stores your data
- Uses inverted indexes for fast full-text search
- Similar to Elasticsearch but open source

### 2. **OpenSearch Java Client**
- Library that communicates with OpenSearch server
- Handles HTTP requests and JSON serialization
- Provides type-safe API for search operations

### 3. **Spring Boot Application** (Port 8080)
- REST API that exposes search functionality
- Layers:
  - **Controller**: HTTP endpoints (`/api/products`)
  - **Service**: Business logic (indexing, searching)
  - **Config**: OpenSearch client setup
  - **Model**: Data structures (Product)

### 4. **OpenSearch Dashboards** (Port 5601)
- Web UI for visualizing and querying data
- Login: admin / Admin@123
- Access: http://localhost:5601

## 🔍 How Search Works

### Indexing Flow:
```
User → REST API → Service Layer → OpenSearch Client → OpenSearch Server
                                                       ↓
                                               Analyze & Index Text
                                                       ↓
                                               Store in Inverted Index
```

### Search Flow:
```
User Search Query → REST API → Service Layer → OpenSearch Query DSL
                                                       ↓
                                               OpenSearch Server
                                                       ↓
                                        Search Inverted Index
                                                       ↓
                                        Rank by Relevance (BM25)
                                                       ↓
User ← REST API ← Service Layer ← OpenSearch Client ← Results
```

## 📝 Core Concepts

### Inverted Index
OpenSearch creates an inverted index that maps words to documents:
```
"laptop" → [doc1, doc5, doc12]
"wireless" → [doc2, doc8]
"high" → [doc1, doc3, doc5]
```

This makes text search extremely fast!

### Text Analysis
When you index "High Performance Laptop":
1. **Tokenization**: Split into ["High", "Performance", "Laptop"]
2. **Lowercase**: ["high", "performance", "laptop"]
3. **Stemming**: ["high", "perform", "laptop"]

When you search for "performs", it matches "performance"!

### Relevance Scoring
OpenSearch uses BM25 algorithm to rank results:
- **Term Frequency**: How often the search term appears
- **Inverse Document Frequency**: How rare/important the term is
- **Field Length**: Shorter fields get higher scores

## 🎯 API Examples

### Index Products
```bash
# Single product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"id":"1","name":"Laptop","description":"High performance","price":999.99,"category":"Electronics","brand":"Dell"}'

# Multiple products (bulk)
curl -X POST http://localhost:8080/api/products/bulk \
  -H "Content-Type: application/json" \
  -d '[{"id":"1","name":"Laptop",...}, {"id":"2","name":"Mouse",...}]'
```

### Search Products
```bash
# Multi-field search (searches name, description, category, brand)
curl "http://localhost:8080/api/products/search?q=laptop"

# Search specific field
curl "http://localhost:8080/api/products/search/field?field=category&value=Electronics"

# Get all products
curl http://localhost:8080/api/products

# Get specific product
curl http://localhost:8080/api/products/1
```

### Delete Product
```bash
curl -X DELETE http://localhost:8080/api/products/1
```

## 🛠️ Troubleshooting

### OpenSearch won't start
```bash
# Check Docker memory allocation (needs at least 4GB)
docker stats

# View logs
docker logs opensearch
```

### Application can't connect
```bash
# Verify OpenSearch is running
curl -XGET "http://localhost:9200" -u admin:Admin@123 -k

# Check application logs for connection errors
# Ensure password in application.yml matches docker-compose.yml
```

### Port already in use
```bash
# Find what's using the port
lsof -i :8080
lsof -i :9200

# Kill the process or change port in application.yml
```

## 🧪 Testing in OpenSearch Dashboards

1. Navigate to http://localhost:5601
2. Login with `admin` / `Admin@123`
3. Go to **Dev Tools** (left menu)
4. Run queries:

```json
# View all products
GET /products/_search
{
  "query": { "match_all": {} }
}

# Search for "laptop"
GET /products/_search
{
  "query": {
    "match": { "name": "laptop" }
  }
}

# Search multiple fields
GET /products/_search
{
  "query": {
    "multi_match": {
      "query": "wireless mouse",
      "fields": ["name", "description"]
    }
  }
}
```

## 📚 Next Steps

1. **Add more fields** to Product model (tags, ratings, reviews)
2. **Implement aggregations** (group by category, price ranges)
3. **Add fuzzy search** (handle typos)
4. **Implement autocomplete** (search suggestions)
5. **Add filters** (price range, category filters)
6. **Pagination** (handle large result sets)
7. **Highlighting** (show matching text snippets)

## 🧹 Cleanup

```bash
# Stop containers
docker-compose down

# Remove all data
docker-compose down -v
```

## 📖 Additional Resources

- OpenSearch Documentation: https://opensearch.org/docs/
- OpenSearch Java Client: https://opensearch.org/docs/latest/clients/java/
- Spring Boot: https://spring.io/projects/spring-boot

---

**Happy Searching! 🔍**
