# Scripts

This directory contains utility scripts for testing and data management.

## Available Scripts

### 📥 [load-sample-data.sh](./load-sample-data.sh)
Loads sample product data into the application for testing and demonstration.

**Usage:**
```bash
# Make sure the application is running first
mvn spring-boot:run

# In another terminal, run:
./scripts/load-sample-data.sh
```

**What it does:**
- Checks if the application is running
- Loads 15 sample products from `data/sample-products.json`
- Verifies successful indexing
- Displays sample search queries to try

### 🧪 [test-api.sh](./test-api.sh)
Automated API testing script that demonstrates all major endpoints.

**Usage:**
```bash
# Make sure the application is running first
mvn spring-boot:run

# In another terminal, run:
./scripts/test-api.sh
```

**What it tests:**
1. Application health check
2. Indexing sample products (bulk operation)
3. Retrieving all products
4. Searching for "laptop"
5. Searching for "wireless"
6. Searching by category "Electronics"
7. Getting a specific product by ID

**Requirements:**
- Application must be running on `http://localhost:8080`
- `curl` command-line tool
- `jq` for pretty JSON output (optional but recommended)

## Making Scripts Executable

If the scripts are not executable, run:
```bash
chmod +x scripts/*.sh
```

## Running from Project Root

All scripts are designed to be run from the project root directory:
```bash
cd /Users/vgarg/opensearch-demo
./scripts/load-sample-data.sh
./scripts/test-api.sh
```

## Troubleshooting

### Script not found
Make sure you're in the project root directory:
```bash
cd /Users/vgarg/opensearch-demo
```

### Permission denied
Make the script executable:
```bash
chmod +x scripts/load-sample-data.sh
chmod +x scripts/test-api.sh
```

### Application not running
Start the application before running scripts:
```bash
# Terminal 1: Start OpenSearch
docker-compose up -d

# Wait ~30 seconds, then start the app
mvn spring-boot:run

# Terminal 2: Run scripts
./scripts/load-sample-data.sh
```

### jq command not found
Install jq for pretty JSON output:
```bash
# macOS
brew install jq

# Ubuntu/Debian
sudo apt-get install jq

# Or the scripts will still work without jq, just without formatting
```

## Creating New Scripts

When adding new scripts:
1. Place them in this directory
2. Make them executable: `chmod +x scripts/your-script.sh`
3. Add documentation here
4. Use relative paths from project root
5. Include error handling and user feedback
