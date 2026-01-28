# Data

This directory contains sample data files for testing and demonstration.

## Files

### 📦 [sample-products.json](./sample-products.json)
Contains 15 sample product records for testing the OpenSearch functionality.

**Product Fields:**
- `id` - Unique product identifier
- `name` - Product name
- `description` - Detailed product description
- `price` - Product price (USD)
- `category` - Product category (Electronics, Home Appliances, Home & Office)
- `brand` - Product brand/manufacturer

**Sample Products Include:**
1. MacBook Pro 16-inch
2. Wireless Gaming Mouse
3. Automatic Coffee Maker
4. USB-C Multiport Hub
5. LED Desk Lamp
6. Mechanical Keyboard
7. Noise Cancelling Headphones
8. Standing Desk Converter
9. Smart Watch
10. Portable SSD
11. Air Purifier
12. Webcam 1080p
13. Electric Kettle
14. Monitor 27-inch 4K
15. Robot Vacuum

## Usage

### Load Sample Data

Use the provided script to load this data:
```bash
./scripts/load-sample-data.sh
```

### Manual Loading

Or load manually via API:
```bash
curl -X POST http://localhost:8080/api/products/bulk \
  -H "Content-Type: application/json" \
  -d @data/sample-products.json
```

### View the Data

```bash
# Pretty print the JSON
cat data/sample-products.json | jq '.'

# Count products
cat data/sample-products.json | jq 'length'

# List product names
cat data/sample-products.json | jq '.[].name'
```

## Adding Your Own Data

You can create additional JSON files in this directory with the same structure:

```json
[
  {
    "id": "unique-id",
    "name": "Product Name",
    "description": "Product description",
    "price": 99.99,
    "category": "Category Name",
    "brand": "Brand Name"
  }
]
```

Then load them using:
```bash
curl -X POST http://localhost:8080/api/products/bulk \
  -H "Content-Type: application/json" \
  -d @data/your-file.json
```

## Data Format

The JSON file should be an array of product objects. Each product must have:
- **id** (string, required): Unique identifier
- **name** (string, required): Product name
- **description** (string, optional): Full description
- **price** (number, optional): Price in USD
- **category** (string, optional): Product category
- **brand** (string, optional): Brand/manufacturer

## Tips

- Keep IDs unique to avoid overwriting existing products
- Make descriptions detailed for better search results
- Use consistent category names for easier filtering
- Test with small datasets first before loading large amounts of data
