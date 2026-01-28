#!/bin/bash

# Script to load sample product data into the application

echo "=========================================="
echo "Loading Sample Product Data"
echo "=========================================="
echo ""

# Check if application is running
echo "Checking if application is running..."
if ! curl -s http://localhost:8080/api/products > /dev/null 2>&1; then
    echo "❌ Application is not running!"
    echo "Please start the application first with: mvn spring-boot:run"
    exit 1
fi

echo "✓ Application is running"
echo ""

# Load sample data
echo "Loading 15 sample products..."
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RESPONSE=$(curl -s -X POST http://localhost:8080/api/products/bulk \
  -H "Content-Type: application/json" \
  -d @"${SCRIPT_DIR}/../data/sample-products.json")

# Check if successful
if [ $? -eq 0 ]; then
    echo "✓ Sample data loaded successfully!"
    echo ""
    echo "Response:"
    echo "$RESPONSE" | jq '.' 2>/dev/null || echo "$RESPONSE"
    echo ""
    echo "=========================================="
    echo "Try these sample searches:"
    echo "=========================================="
    echo ""
    echo "1. Search for laptops:"
    echo "   curl 'http://localhost:8080/api/products/search?q=laptop'"
    echo ""
    echo "2. Search for wireless products:"
    echo "   curl 'http://localhost:8080/api/products/search?q=wireless'"
    echo ""
    echo "3. Search Electronics category:"
    echo "   curl 'http://localhost:8080/api/products/search/field?field=category&value=Electronics'"
    echo ""
    echo "4. Search by brand (Apple):"
    echo "   curl 'http://localhost:8080/api/products/search/field?field=brand&value=Apple'"
    echo ""
    echo "5. Get all products:"
    echo "   curl 'http://localhost:8080/api/products'"
    echo ""
else
    echo "❌ Failed to load sample data"
    exit 1
fi
