#!/bin/bash

echo "Building Tofimotia application..."
./mvnw clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "Build successful! Starting application with local profile..."
    echo "Application will be available at: http://localhost:8080"
    echo "H2 Console will be available at: http://localhost:8080/h2-console"
    echo "Swagger UI will be available at: http://localhost:8080/swagger-ui.html"
    echo ""
    echo "Press Ctrl+C to stop the application"
    echo ""
    
    java -jar target/Tofimotia-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
else
    echo "Build failed! Please check the errors above."
    exit 1
fi