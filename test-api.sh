#!/bin/bash

# Script to test the OpenSearch Demo API

echo "=========================================="
echo "Testing OpenSearch Demo API"
echo "=========================================="
echo ""

# Check if the application is running
echo "1. Checking if application is running..."
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1 || curl -s http://localhost:8080/api/products > /dev/null 2>&1; then
    echo "✓ Application is running"
else
    echo "⚠ Application may not be running. Make sure to start it with: mvn spring-boot:run"
fi
echo ""

# Index sample products
echo "2. Indexing sample products..."
curl -X POST http://localhost:8080/api/products/bulk \
  -H "Content-Type: application/json" \
  -d '[
    {
      "id": "1",
      "name": "MacBook Pro",
      "description": "High performance laptop with 16GB RAM and M2 chip",
      "price": 1999.99,
      "category": "Electronics",
      "brand": "Apple"
    },
    {
      "id": "2",
      "name": "Wireless Mouse",
      "description": "Ergonomic wireless mouse with 6 programmable buttons",
      "price": 29.99,
      "category": "Electronics",
      "brand": "Logitech"
    },
    {
      "id": "3",
      "name": "Coffee Maker",
      "description": "Automatic drip coffee maker with timer and thermal carafe",
      "price": 79.99,
      "category": "Home Appliances",
      "brand": "Cuisinart"
    },
    {
      "id": "4",
      "name": "USB-C Hub",
      "description": "7-in-1 USB-C hub with HDMI, USB 3.0, and SD card reader",
      "price": 39.99,
      "category": "Electronics",
      "brand": "Anker"
    },
    {
      "id": "5",
      "name": "Desk Lamp",
      "description": "LED desk lamp with adjustable brightness and color temperature",
      "price": 45.99,
      "category": "Home & Office",
      "brand": "TaoTronics"
    }
  ]'
echo ""
echo ""

# Get all products
echo "3. Getting all products..."
curl -s http://localhost:8080/api/products | jq '.'
echo ""
echo ""

# Search for "laptop"
echo "4. Searching for 'laptop'..."
curl -s "http://localhost:8080/api/products/search?q=laptop" | jq '.'
echo ""
echo ""

# Search for "wireless"
echo "5. Searching for 'wireless'..."
curl -s "http://localhost:8080/api/products/search?q=wireless" | jq '.'
echo ""
echo ""

# Search by category
echo "6. Searching by category 'Electronics'..."
curl -s "http://localhost:8080/api/products/search/field?field=category&value=Electronics" | jq '.'
echo ""
echo ""

# Get specific product
echo "7. Getting product with ID '1'..."
curl -s http://localhost:8080/api/products/1 | jq '.'
echo ""
echo ""

echo "=========================================="
echo "Testing Complete!"
echo "=========================================="
echo ""
echo "You can also access:"
echo "  - Application: http://localhost:8080"
echo "  - OpenSearch: http://localhost:9200 (admin/Admin@123)"
echo "  - OpenSearch Dashboards: http://localhost:5601 (admin/Admin@123)"
