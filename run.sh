#!/bin/bash

# Set default port if not provided
export PORT=${PORT:-8080}

# Set default API key if not provided
export VORTEX_API_KEY=${VORTEX_API_KEY:-demo-api-key}

echo "☕ Starting Vortex Java SDK Demo"
echo "📱 Port: $PORT"
echo "🔧 API Key: ${VORTEX_API_KEY:0:10}..."
echo ""

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven not found. Please install Maven to run the demo."
    echo "   Visit: https://maven.apache.org/install.html"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2 | cut -d '.' -f 1-2)
if [[ "$JAVA_VERSION" < "17" ]]; then
    echo "❌ Java 17 or later required. Found: $JAVA_VERSION"
    echo "   Please install Java 17+ to run the demo."
    exit 1
fi

# Build dependencies if needed
cd "$(dirname "$0")"

# Check if Vortex SDK is available, if not build and install it
if ! mvn dependency:resolve -q 2>/dev/null; then
    echo "📦 Installing Vortex Java SDK to local Maven repository..."
    cd ../../packages/vortex-java-sdk
    mvn clean install -q
    if [ $? -ne 0 ]; then
        echo "❌ Failed to install Vortex Java SDK"
        exit 1
    fi
    cd - > /dev/null
    echo "✅ Vortex Java SDK installed successfully"
fi

# Run the application
mvn spring-boot:run