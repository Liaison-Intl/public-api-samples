# CAS API Python Scripts

This repository contains Python scripts for interacting with the Liaison CAS (Centralized Application Service) API. These scripts demonstrate how to retrieve application data and documents from the CAS API.

## Scripts Overview

### 1. `CAS_API_Retrieve_Documents.py`
- **Purpose**: Downloads documents (PDFs, transcripts, etc.) from CAS applications
- **Functionality**: 
  - Authenticates with the CAS API
  - Retrieves organizations, programs, and applications
  - Downloads application documents as ZIP files
  - Creates CSV indexes of downloaded documents
- **Output**: Documents saved to `output/` directory with timestamps

### 2. `CAS_API-Retrieve_Data_as_JSON.py`
- **Purpose**: Retrieves application data in JSON format
- **Functionality**:
  - Authenticates with the CAS API
  - Retrieves organizations, programs, and applications
  - Downloads application data as JSON files
  - Creates flat file exports of application data
- **Output**: JSON data files saved to `output/` directory with timestamps

## Prerequisites

- Python 3.7 or higher
- Access to Liaison CAS API (API key, username, password)
- Valid Application Form ID, Organization ID, and Program ID

## Setup Instructions

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

## Configuration

Before running the scripts, you need to update the following variables in each script:

### API Credentials
```python
apiKey = "your-api-key-here"
UserName = "your-username-here"
Password = "your-password-here"
```

### Application IDs
```python
applicationFormId = 1111  # Your Application Form ID
organizationId = 22222    # Your Organization ID
programId = 333333        # Your Program ID
```

### Output Directory
```python
saveDir = "/path/to/your/output/directory/"
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

2. **Authentication Errors**: Verify your API credentials are correct

3. **Permission Errors**: Ensure the output directory is writable

4. **Network Issues**: Check your internet connection and API endpoint accessibility

## License

This code is provided as-is for educational and demonstration purposes. Please refer to Liaison's terms of service for API usage guidelines. 