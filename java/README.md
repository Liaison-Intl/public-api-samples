# UNICAS Application Integration Sample Client

A Java-based sample client for integrating with the UNICAS (Unified College Application Service) API. This project demonstrates how to interact with the UNICAS Applicant API suite, including application retrieval, program information, and file downloads.

## 🚀 Features

- **Applicant API Integration**: Retrieve application submissions with configurable scopes (form, organization, program level)
- **Program Information**: Access program details and branding information
- **File Downloads**: Download application files including transcripts and other documents
- **Configuration Management**: Flexible configuration through properties files
- **Interactive CLI**: User-friendly command-line interface with menu-driven options
- **GZIP Compression Support**: Optimized data transfer with compression
- **Token Management**: Automatic token refresh and authentication handling
- **JSON Processing**: Advanced JSON path matching and data transformation

## 📋 Prerequisites

- Java 8 or higher
- Maven 3.6+
- UNICAS API credentials (username, password, API key)
- Access to UNICAS API endpoints

## 🛠️ Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/unicas-application-integration-sample-client.git
   cd unicas-application-integration-sample-client
   ```

2. **Build the project**:
   ```bash
   mvn clean package
   ```

3. **Configure the application**:
   Edit `src/main/resources/application.properties` with your UNICAS API credentials and settings.

## ⚙️ Configuration

Configure your API settings in `src/main/resources/application.properties`:

```properties
# API Configuration
unicas.api.url=https://your-unicas-api-endpoint.com
unicas.api.username=your_username
unicas.api.password=your_password
unicas.api.key=your_api_key

# Application Settings
unicas.prop.applicationFormId=your_form_id
unicas.prop.organizationId=your_org_id
unicas.prop.programId=your_program_id

# API Scope: form, org, prog (cannot exceed user permissions)
# form: requires applicationFormId
# org: requires applicationFormId + organizationId  
# prog: requires applicationFormId + organizationId + programId
unicas.prop.apiScope=form

# Date filters (yyyy-MM-dd format, inclusive)
unicas.prop.fromDate=2024-01-01
unicas.prop.toDate=2024-12-31

# Optional settings
unicas.prop.skipFiles=false
unicas.prop.useGzip=true
```

### API Scope Configuration

- **`form`**: Form-level access - requires `applicationFormId`
- **`org`**: Organization-level access - requires `applicationFormId` and `organizationId`
- **`prog`**: Program-level access - requires `applicationFormId`, `organizationId`, and `programId`

## 🚀 Usage

### Running the Application

```bash
# Run the interactive client
java -jar target/unicas-api-sample-client.jar

# Run with custom properties file
java -jar target/unicas-api-sample-client.jar -p /path/to/custom.properties

# Print available properties
java -jar target/unicas-api-sample-client.jar -s

# Set output directory
java -jar target/unicas-api-sample-client.jar -o /path/to/output/directory

# Show help
java -jar target/unicas-api-sample-client.jar -h
```

### Interactive Menu Options

When you run the application, you'll see an interactive menu with the following options:

1. **Exercise Applicant API suite** - Basic application retrieval
2. **Exercise full Applicant API suite** - Complete suite with configuration and lookup values
3. **Exercise GET Programs/Program Branding suite** - Program information retrieval
4. **Re-enter initial parameters** - Update configuration at runtime
5. **Exit** - Close the application

### Command Line Options

| Option | Description |
|--------|-------------|
| `-h` | Display help information |
| `-p <file>` | Use custom properties file |
| `-s` | Print list of supported properties |
| `-o <dir>` | Set output directory for files |
| `-n <limit>` | Set application limit |

## 📁 Project Structure

```
src/main/java/com/liaisonedu/
├── client/           # API client implementation
├── constants/        # Application constants
├── entity/          # Data models and DTOs
├── matcher/         # JSON path matching utilities
├── request/         # API request handlers
└── util/            # Utility classes and main application
```

### Key Components

- **`UnicasApiClient`**: Main API client for HTTP communication
- **`Main`**: Application entry point with interactive menu
- **`ApplicationRequest`**: Handles application-related API calls
- **`ProgramRequest`**: Manages program information requests
- **`FileRequest`**: Handles file downloads
- **`JsonPathMatcher`**: Advanced JSON processing and matching

## 🔧 Development

### Building from Source

```bash
# Compile and package
mvn clean package

# Run tests
mvn test

# Install to local repository
mvn install
```

### Dependencies

The project uses the following key dependencies:

- **Apache HttpClient 4.5.6**: HTTP client for API communication
- **Jackson 2.9.7**: JSON processing
- **Jayway JsonPath 2.4.0**: JSON path expressions
- **SLF4J 1.7.25**: Logging framework
- **JUnit 5.3.2**: Testing framework
- **Mockito 3.12.4**: Mocking framework

## 📊 API Endpoints

The client supports the following UNICAS API endpoints:

- **Applications**: Retrieve application submissions
- **Programs**: Get program information by organization
- **Program Branding**: Access program branding details
- **Configuration**: Retrieve configuration information
- **Lookups**: Access lookup values
- **Files**: Download application files

## 🔐 Security

- API credentials are stored in configuration files
- Automatic token refresh on expiration
- Support for API key authentication
- GZIP compression for secure data transfer

## 📝 Logging

The application uses SLF4J with Log4j for logging. Log configuration can be found in `src/main/resources/log4j.properties`.

## 📞 Contact

For questions, support, or feedback regarding this UNICAS API integration sample client, please contact:

**Jimmy Henson**  
Email: jhenson@liaisonedu.com

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Note**: This is a sample client for demonstration purposes. For production use, ensure proper security measures and error handling are implemented according to your organization's standards.