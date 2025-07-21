# CAS API Python Samples

## Project Overview

This repository contains Python sample scripts for interacting with the Liaison Education CAS (Centralized Application Service) API. These scripts demonstrate how to authenticate, retrieve data, and download documents from various CAS platforms.

**@created**     2019-09-03  
**@version**     1.1  
**@license**     Proprietary - Liaison Education  
**@copyright**   Copyright (c) 2019 Liaison Education  

## Description

The CAS API Python samples provide comprehensive examples for:
- **Authentication**: Secure API access using API keys and credentials
- **Data Retrieval**: Extracting application data in JSON format
- **Document Management**: Downloading and organizing application documents
- **Organization Discovery**: Finding available organizations and programs
- **Application Processing**: Working with application submissions and metadata

### Key Features

- 🔐 **Secure Authentication**: OAuth token-based authentication with automatic refresh
- 📊 **Data Export**: JSON data retrieval with tabular presentation
- 📄 **Document Downloads**: PDF and document retrieval with ZIP packaging
- 🏢 **Multi-Organization Support**: Work with multiple CAS organizations
- 📅 **Date Filtering**: Filter applications by date ranges
- 📋 **CSV Indexing**: Generate searchable indexes for downloaded documents

## Prerequisites

### Required Software
- Python 3.7 or higher
- pip (Python package manager)

### Required Python Packages
```bash
pip install requests beautifultable
```

### API Access
- Valid CAS API credentials (API key, username, password)
- Access to specific Application Form IDs
- Appropriate permissions for target organizations and programs

## Installation

### 1. Clone or Download the Repository
```bash
git clone <repository-url>
cd CAS_API
```

### 2. Create a Virtual Environment
```bash
python3 -m venv venv
```

### 3. Activate the Virtual Environment
```bash
# On macOS/Linux:
source venv/bin/activate

# On Windows:
venv\Scripts\activate
```

### 4. Install Dependencies
```bash
pip install -r requirements.txt
```

### 5. Configure Credentials
Update the following variables in each script:
- Set `apiKey`, `UserName`, and `Password` variables
- Configure `saveDir` for local file storage
- Set appropriate Application Form ID, Organization ID, and Program ID

## Scripts Overview

### 1. CAS_API_Retrieve_Documents.py

**@description**  
Retrieves application documents (PDFs, transcripts, etc.) from the CAS API and packages them into organized ZIP files with CSV indexes.

**@features**
- Document download and ZIP file creation
- CSV index generation for downloaded documents
- Organization and program discovery
- Application filtering by date ranges
- Progress tracking and error handling

**@usage**
```bash
# Method 1: Using Virtual Environment Python Directly (Recommended)
./venv/bin/python CAS_API_Retrieve_Documents.py

# Method 2: Activate Virtual Environment First
source venv/bin/activate
python CAS_API_Retrieve_Documents.py
deactivate
```

**@output**
- ZIP files containing downloaded documents
- CSV index files for document organization
- Console output with progress information

### 2. CAS_API-Retrieve_Data_as_JSON.py

**@description**  
Retrieves application data in JSON format from the CAS API for data analysis and processing.

**@features**
- JSON data export and file management
- Tabular data presentation for review
- Organization and program discovery
- Application filtering by date ranges
- Structured data extraction

**@usage**
```bash
# Method 1: Using Virtual Environment Python Directly (Recommended)
./venv/bin/python CAS_API-Retrieve_Data_as_JSON.py

# Method 2: Activate Virtual Environment First
source venv/bin/activate
python CAS_API-Retrieve_Data_as_JSON.py
deactivate
```

**@output**
- JSON files containing application data
- Console tables for data review
- Structured data for analysis

## Configuration

### API Credentials
```python
apiKey = "your-api-key-here"
UserName = "your-username-here"
Password = "your-password-here"
```

### Environment Settings
```python
baseUrl = "https://api.liaisonedu.com"  # Production environment
saveDir = "/path/to/your/save/directory/"
```

### Application Settings
```python
applicationFormId = 12345  # Your Application Form ID
organizationId = 67890     # Your Organization ID
programId = 11111          # Your Program ID
```

## Running the Scripts

### Method 1: Using Virtual Environment Python Directly (Recommended)
```bash
# Run the documents script
./venv/bin/python CAS_API_Retrieve_Documents.py

# Run the JSON data script
./venv/bin/python CAS_API-Retrieve_Data_as_JSON.py
```

### Method 2: Activate Virtual Environment First
```bash
# Activate the virtual environment
source venv/bin/activate

# Run the scripts
python CAS_API_Retrieve_Documents.py
python CAS_API-Retrieve_Data_as_JSON.py

# Deactivate when done
deactivate
```

### Method 3: Create Shell Scripts (Optional)
Create executable shell scripts for convenience:

```bash
# Create run_documents.sh
echo '#!/bin/bash
./venv/bin/python CAS_API_Retrieve_Documents.py' > run_documents.sh

# Create run_json.sh
echo '#!/bin/bash
./venv/bin/python CAS_API-Retrieve_Data_as_JSON.py' > run_json.sh

# Make them executable
chmod +x run_documents.sh run_json.sh

# Run them
./run_documents.sh
./run_json.sh
```

## What the Scripts Do

### Authentication Flow
1. **Sign In**: Authenticates with the CAS API using your credentials
2. **Get Token**: Receives an authorization token valid for 1 hour
3. **API Calls**: Uses the token for all subsequent API requests

### Data Retrieval Process
1. **Get Organizations**: Retrieves list of organizations you have access to
2. **Get Programs**: Retrieves programs offered by the selected organization
3. **Get Program Details**: Retrieves specific program information
4. **Get Applications**: Retrieves applications for the selected program
5. **Process Data**: Downloads documents or exports JSON data

### Output Structure
```
output/
├── CASDocuments-{formId}_{orgId}_{programId}_{timestamp}/
│   ├── documents.zip
│   └── document_index.csv
└── CASData-{formId}_{orgId}_{programId}_{timestamp}/
    ├── applications.json
    └── flat_file_export.csv
```

## Usage Examples

### Basic Document Retrieval
1. Configure credentials and IDs
2. Run the document retrieval script
3. Review generated ZIP files and CSV indexes

### Data Export with Date Filtering
1. Set date range parameters
2. Configure output directory
3. Execute JSON data retrieval
4. Analyze exported data

### Multi-Organization Processing
1. List available organizations
2. Iterate through organizations
3. Process each organization's data
4. Organize output by organization

## API Endpoints Used

### Authentication
- `POST /v1/auth/token` - Obtain authorization token

### Organization Management
- `GET /v1/applicationForms/{id}/organizations` - List organizations
- `GET /v1/applicationForms/{id}/organizations/{orgId}/programs` - List programs

### Application Data
- `GET /v1/applicationForms/{id}/organizations/{orgId}/programs/{progId}/applications` - List applications
- `GET /v1/applicationForms/{id}/organizations/{orgId}/programs/{progId}/applications/{appId}` - Get application details

### Document Management
- `GET /v1/applicationForms/{id}/organizations/{orgId}/programs/{progId}/applications/{appId}/documents` - List documents
- `GET /v1/applicationForms/{id}/organizations/{orgId}/programs/{progId}/applications/{appId}/documents/{docId}` - Download document

## Error Handling

The scripts include comprehensive error handling for:
- Authentication failures
- API rate limiting
- Network connectivity issues
- Invalid credentials or permissions
- File system errors

## Security Considerations

- **Credential Storage**: Store API credentials securely, not in version control
- **Token Management**: Tokens expire after 1 hour; scripts handle refresh automatically
- **Data Privacy**: Downloaded data contains sensitive application information
- **Access Control**: Ensure appropriate file system permissions for output directories

## Dependencies

The scripts require the following Python packages:
- `requests` - HTTP library for API calls
- `beautifultable` - Library for displaying tabular data
- Standard libraries: `time`, `datetime`, `os`, `zipfile`, `csv`

## Troubleshooting

### Common Issues

1. **ModuleNotFoundError**: Make sure you're using the virtual environment
   ```bash
   ./venv/bin/python script.py
   ```

2. **Authentication Errors**
   - Verify API credentials are correct
   - Check API key permissions
   - Ensure account has access to target Application Form ID

3. **No Data Returned**
   - Verify Application Form ID, Organization ID, and Program ID
   - Check date range filters
   - Confirm account has access to requested data

4. **File System Errors**
   - Verify save directory exists and is writable
   - Check available disk space
   - Ensure proper file permissions

5. **Network Issues**: Check your internet connection and API endpoint accessibility

### Debug Mode
Enable verbose logging by modifying the scripts to include additional print statements for debugging API responses and file operations.

## Support

For technical support or questions about the CAS API:
- **API Documentation**: https://api.liaisonedu.com/reference/swagger-ui/index.html
- **Contact**: Jimmy Henson jhenson@liaisonedu.com
- **Liaison Support**: Contact your Liaison customer service representative

## License

This software is proprietary to Liaison Education. Unauthorized copying, distribution, or use is prohibited.


---

**@see** https://api.liaisonedu.com/reference/swagger-ui/index.html  
**@since** 2019-09-03  
**@deprecated** None 