# Liaison Public API Samples

This repository contains sample code and implementations for integrating with Liaison's Centralized Application Service (CAS) API across multiple programming languages. These samples demonstrate how to authenticate, retrieve application data, and download documents from the CAS API.

## Overview

The CAS API allows educational institutions to programmatically access application data, including:
- Application submissions and metadata
- Supporting documents (transcripts, evaluations, attachments)
- Program and organization information
- Configuration and lookup data

## Available Implementations

### 🐍 Python Implementation
**Location**: [`python/CAS_API/`](python/CAS_API/)

**Features**:
- Complete CAS API integration with authentication
- Document download functionality (PDFs, transcripts, etc.)
- JSON data export capabilities
- Comprehensive error handling and logging
- Virtual environment setup with dependency management

**Key Scripts**:
- `CAS_API_Retrieve_Documents.py` - Downloads application documents as ZIP files
- `CAS_API-Retrieve_Data_as_JSON.py` - Exports application data in JSON format

**Documentation**: See [Python README](python/CAS_API/README.md) for detailed setup and usage instructions.

**Requirements**:
- Python 3.7+
- Access to Liaison CAS API credentials
- Valid Application Form ID, Organization ID, and Program ID

### ☕ Java Implementation
**Location**: [`java/AWS_API_usage_sample/`](java/AWS_API_usage_sample/)

**Features**:
- Maven-based project structure
- Comprehensive API client implementation
- Support for various CAS API endpoints
- Logging and error handling
- Test data and configuration management

**Status**: This implementation appears to be compiled but source files may need to be restored from version control history.

**Key Components**:
- `UnicasApiClient` - Main API client class
- `TokenRequest` - Authentication handling
- `ApplicationRequest` - Application data retrieval
- `FileRequest` - Document download functionality

**Requirements**:
- Java 8+
- Maven for dependency management
- Valid CAS API credentials

### 🐘 PHP Implementation
**Location**: [`php/php-sample-cas-api-client-master/`](php/php-sample-cas-api-client-master/)

**Features**:
- Composer-based dependency management
- Guzzle HTTP client integration
- PSR-7 compliant HTTP messaging
- Environment variable configuration

**Status**: This implementation has vendor dependencies installed but source files may need to be added.

**Dependencies**:
- PHP 7.2.5+
- Composer for dependency management
- Guzzle HTTP client
- PSR-7 HTTP message interfaces

## Getting Started

### Prerequisites

Before using any of these implementations, you'll need:

1. **API Access**: Valid CAS API credentials from Liaison
   - API Key
   - Username
   - Password

2. **Application IDs**: Specific identifiers for your use case
   - Application Form ID
   - Organization ID
   - Program ID

3. **Development Environment**: Appropriate runtime for your chosen language
   - Python 3.7+ for Python implementation
   - Java 8+ for Java implementation
   - PHP 7.2.5+ for PHP implementation

### Quick Start

1. **Choose your implementation** based on your preferred programming language
2. **Navigate to the project directory** for your chosen language
3. **Follow the setup instructions** in the respective project's documentation
4. **Configure your credentials** in the appropriate configuration files
5. **Run the sample scripts** to test your integration

## API Authentication

All implementations use the same authentication flow:

1. **Sign In**: Authenticate with the CAS API using your credentials
2. **Get Token**: Receive an authorization token (valid for 1 hour)
3. **API Calls**: Use the token for all subsequent API requests

## Common API Endpoints

The implementations demonstrate usage of these key CAS API endpoints:

- `POST /v1/auth/token` - Authentication
- `GET /v1/applicationForms/{formId}/organizations/{orgId}/applications` - Retrieve applications
- `GET /v1/applicationForms/{formId}/organizations/{orgId}/programs` - Retrieve programs
- `GET /v1/applicationForms/{formId}/organizations/{orgId}/files/{fileId}` - Download documents

## Output Formats

### Document Downloads
- PDF files organized by application
- ZIP archives with document indexes
- CSV files for document metadata

### Data Exports
- JSON format for structured data
- Flat file exports for analysis
- Timestamped output directories

## Troubleshooting

### Common Issues

1. **Authentication Errors**
   - Verify API credentials are correct
   - Check API key permissions
   - Ensure account has proper access levels

2. **Missing Dependencies**
   - Python: Run `pip install -r requirements.txt`
   - Java: Run `mvn clean install`
   - PHP: Run `composer install`

3. **Configuration Issues**
   - Verify Application Form ID, Organization ID, and Program ID
   - Check output directory permissions
   - Ensure network connectivity to API endpoints

### Logging

All implementations include comprehensive logging:
- Python: Console output with timestamps
- Java: Log files in `logs/` directory
- PHP: Error reporting and debug output

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


**Note**: These are sample implementations provided for educational and demonstration purposes. Please refer to Liaison's official API documentation and terms of service for production use. 