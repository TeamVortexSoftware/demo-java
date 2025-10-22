#!/bin/bash

echo "🔧 Building Vortex Java Demo"
echo "============================="

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven not found. Please install Maven to build the demo."
    echo "   Visit: https://maven.apache.org/install.html"
    exit 1
fi

# Build and install the Vortex Java SDK first
echo "📦 Building Vortex Java SDK..."
cd "$(dirname "$0")/../../packages/vortex-java-sdk"
mvn clean install -q

if [ $? -ne 0 ]; then
    echo "❌ Failed to build Vortex Java SDK"
    exit 1
fi

echo "✅ Vortex Java SDK built successfully"

# Now build the demo app
echo "📱 Building demo application..."
cd "$(dirname "$0")"
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ Demo application built successfully"
    echo ""
    echo "🚀 Run the demo with: ./run.sh"
else
    echo "❌ Failed to build demo application"
    exit 1
fi