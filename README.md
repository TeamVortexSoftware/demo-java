# Vortex Java SDK Demo

A Spring Boot demo application showcasing the Vortex Java SDK integration.

## Features

- ☕ **Spring Boot Integration**: Complete Spring Boot app with auto-configuration
- 🔐 **Authentication System**: Session-based auth with JWT tokens
- ⚡ **Vortex Integration**: Full Vortex API integration for invitation management
- 🎯 **JWT Generation**: Generate Vortex JWTs for authenticated users
- 📧 **Invitation Management**: Get, accept, revoke, and reinvite functionality
- 👥 **Group Management**: Handle invitations by group type and ID
- 🌐 **Interactive Frontend**: Complete HTML interface to test all features

## Prerequisites

- Java 17 or later
- Maven 3.6 or later
- The Vortex Java SDK (automatically linked via workspace)

## Installation

1. Navigate to the demo directory:
   ```bash
   cd apps/demo-java
   ```

2. Install dependencies:
   ```bash
   mvn clean install
   ```

## Running the Demo

1. Set your Vortex API key (optional - defaults to demo key):
   ```bash
   export VORTEX_API_KEY=your-api-key-here
   ```

2. Set the port (optional - defaults to 8080):
   ```bash
   export PORT=8080
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

   Or run the compiled JAR:
   ```bash
   java -jar target/demo-java-1.0.0.jar
   ```

4. Open your browser and visit: `http://localhost:8080`

## Demo Users

The demo includes two test users using the **new simplified JWT format**:

| Email | Password | Auto-Join Admin | Legacy Role |
|-------|----------|-----------------|-------------|
| admin@example.com | password123 | Yes | admin |
| user@example.com | userpass | No | user |

The demo showcases both the new simplified format (`userEmail` + `userIsAutoJoinAdmin`) and the legacy format (`identifiers` + `groups` + `role`) for educational purposes. See [VortexConfiguration.java](src/main/java/com/vortexsoftware/demo/config/VortexConfiguration.java) for implementation details.

## JWT Format

This demo uses Vortex's **new JWT format with User constructor**:

```java
// Create a User with admin scopes
List<String> adminScopes = new ArrayList<>();
if (demoUser.isAutoJoinAdmin()) {
    adminScopes.add("autoJoin");
}

User user = new User(
    demoUser.getId(),      // id
    demoUser.getEmail(),   // email
    adminScopes            // adminScopes (optional)
);

// Generate JWT
String jwt = vortexClient.generateJwt(user);

// Or with extra properties
Map<String, Object> extra = new HashMap<>();
extra.put("role", "admin");
extra.put("department", "Engineering");
String jwt = vortexClient.generateJwt(user, extra);
```

The JWT payload includes:
- `userId`: User's unique ID
- `userEmail`: User's email address
- `userIsAutoJoinAdmin`: Set to `true` when `adminScopes` contains `"autoJoin"`
- Any additional properties from the extra parameter

This replaces the legacy format with identifiers, groups, and role fields.

The demo uses the VortexUser constructor format in VortexConfiguration.java (lines 69-73) which automatically creates the User object with the correct format.

## API Endpoints

### Authentication Routes
- `POST /api/auth/login` - Login with email/password
- `POST /api/auth/logout` - Logout (clears session cookie)
- `GET /api/auth/me` - Get current user info

### Demo Routes
- `GET /api/demo/users` - Get all demo users
- `GET /api/demo/protected` - Protected route (requires auth)

### Vortex API Routes
All Vortex routes require authentication and are auto-configured by the SDK:

- `POST /api/vortex/jwt` - Generate Vortex JWT
- `GET /api/vortex/invitations` - Get invitations by target
- `GET /api/vortex/invitations/{id}` - Get specific invitation
- `DELETE /api/vortex/invitations/{id}` - Revoke invitation
- `POST /api/vortex/invitations/accept` - Accept invitations
- `GET /api/vortex/invitations/by-group/{type}/{id}` - Get group invitations
- `DELETE /api/vortex/invitations/by-group/{type}/{id}` - Delete group invitations
- `POST /api/vortex/invitations/{id}/reinvite` - Reinvite user

### Health Check
- `GET /health` - Server health status

## Configuration

The demo supports the following environment variables:

- `VORTEX_API_KEY`: Your Vortex API key (defaults to "demo-api-key")
- `PORT`: Server port (defaults to 8080)
- `VORTEX_API_BASE_URL`: Vortex API base URL (uses SDK default)

You can also configure these in `application.yml`:

```yaml
server:
  port: 8080

vortex:
  api:
    key: your-api-key-here
    base-url: https://api.vortexsoftware.com
```

## Project Structure

```
apps/demo-java/
├── src/main/java/com/vortexsoftware/demo/
│   ├── DemoJavaApplication.java      # Main Spring Boot application
│   ├── config/
│   │   ├── SecurityConfig.java       # Security configuration
│   │   └── VortexConfiguration.java  # Vortex SDK configuration
│   ├── controller/
│   │   ├── AuthController.java       # Authentication endpoints
│   │   ├── DemoController.java       # Demo endpoints
│   │   └── HealthController.java     # Health check
│   ├── model/
│   │   ├── DemoUser.java            # User model
│   │   └── UserGroup.java           # Group model
│   └── service/
│       └── AuthService.java         # Authentication service
├── src/main/resources/
│   ├── application.yml               # Application configuration
│   └── static/
│       └── index.html               # Frontend interface
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## Development

The demo uses Spring Boot with:

- Spring Web for REST endpoints
- Spring Security (configured to allow all for demo)
- JWT for session management
- Static file serving for the frontend
- Auto-configuration for the Vortex SDK

## Testing the Demo

1. **Login**: Use one of the demo users to authenticate
2. **Generate JWT**: Test Vortex JWT generation for the authenticated user
3. **Query Invitations**: Test invitation queries by target (email, username, etc.)
4. **Group Operations**: Test group-based invitation operations
5. **Health Check**: Verify the server status and configuration

The frontend provides an interactive interface to test all functionality without needing external tools.

## Integration Notes

This demo shows how to integrate the Vortex Java SDK with:

- Spring Boot applications
- Spring Security authentication
- Session-based authentication systems
- REST API development
- Frontend applications (static HTML/JS)
- Environment-based configuration

The same patterns can be applied to other Java web frameworks or standalone applications.

## Building for Production

```bash
# Build the application
mvn clean package

# Run the JAR file
java -jar target/demo-java-1.0.0.jar
```

## Docker Support

You can also run the demo with Docker:

```dockerfile
FROM openjdk:17-jre-slim
COPY target/demo-java-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Troubleshooting

1. **Port conflicts**: Change the PORT environment variable if 8080 is in use
2. **Maven issues**: Make sure you have Java 17+ and Maven 3.6+
3. **API errors**: Check the Vortex API key and network connectivity
4. **Authentication issues**: Clear browser cookies and try logging in again
5. **SDK issues**: Make sure the Vortex Java SDK is properly installed in your local Maven repository