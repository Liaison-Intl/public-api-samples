"""
CAS API JSON Data Retrieval Module
==================================

This module provides functionality to retrieve application data as JSON from the CAS (Centralized Application Service) API.
It handles authentication, API interactions, and data export for Liaison Education's CAS platform.

@created     2019-09-03
@version     1.1
@license     Proprietary - Liaison Education
@copyright   Copyright (c) 2019 Liaison Education

@description
    This script enables automated retrieval of application data in JSON format from the CAS API.
    It supports extracting application information, demographics, academic records, and other
    structured data for applications submitted through various CAS platforms (CASPA, GradCAS, SOPHAS, etc.).
    
    Key Features:
    - Secure authentication with API key and credentials
    - Organization and program discovery
    - Application listing and filtering by date ranges
    - JSON data export and file management
    - Tabular data presentation for review
    
    Prerequisites:
    - Valid CAS API credentials (API key, username, password)
    - Access to specific Application Form IDs
    - Required Python packages: requests, beautifultable, datetime, os
    
    Usage:
    1. Configure API credentials and save directory
    2. Set Application Form ID, Organization ID, and Program ID
    3. Run script to retrieve JSON data
    4. Review generated data files and tables

@see        https://api.liaisonedu.com/reference/swagger-ui/index.html
@since      2019-09-03
@deprecated None
"""


# Prepare Environment
# Make sure you have all the necessary libraries to interact with the API and access target directories
import requests  # make http requests, including API calls
from beautifultable import BeautifulTable   # present tables in output to easily review API responses
import time  # pause in between file generation status checks
from datetime import datetime, timedelta   # retrieve timestamp information for unique identification of downloaded files
import os   # find local directories

# Credentials
# Once you've been granted access, you'll be given credentials for accessing the CAS API:
# API key, username, and password.
# Store these credentials in a secure location; you'll need to use them every time you interact with the CAS API.
apiKey = ""
UserName = ""
Password = ""

# Root URLs
# The root URL to use for the CAS API can depend on which CAS and environment (production, prelaunch) you want to use.
# For production data, the default root URL is https://api.liaisonedu.com.
# Unless otherwise informed, this is the root URL to use.
# Contact Liaison customer service if you're unsure of which root URL to use.
baseUrl = "https://api.liaisonedu.com"

# Local Save Directory
# Choose where you want to download data to on your local machine ONLY IF RETRIEVING DATA AS FLAT FILE
saveDir = ""

# Runtime Timestamp
# Set a timestamp to be use in filenames to more clearly identify the results of script operations
startTime = datetime.now()


# Authorization
# To successfully make any calls to the CAS API, you'll need an authorization token.
# The authorization call is the first call you'll make in any interaction with the CAS API.
# The token is valid for one hour, so you may need to reauthorize if your operation lasts a long time.
# To retrieve an authorization token, make a call to the "Sign In" endpoint
# (POST /v1/auth/token
# https://api.liaisonedu.com/reference/index.htm#/operations/Security/signIn).
# You'll need to include two headers:
# Content-Type: "application/json"
# x-api-key: "{{your API key here}}"
# You'll also need to include your credentials in the body of the request:
# {
# 	"UserName": "{{your username here}}"
# 	,"Password": "{{your password here}}"
# }
# The response to this call will include a "Token" element.
# This "Token", along with your API key, must be included in the headers of all subsequent calls to the CAS API.
# Your calls will all have these two headers:
# x-api-key: "{{your API key here}}"
# Authorization: "{{your auth Token here}}"
def auth(apiKey, UserName, Password):
    """
    Authorize session for CAS API

    Authenticates the user with the CAS API using provided credentials and returns
    an authorization token that must be included in all subsequent API calls.

    @param apiKey    The user's CAS API key for authentication
    @param UserName  The username for the CAS API account
    @param Password  The password for the CAS API account
    @return          Authorization token string to be used with all API requests
    @throws          requests.RequestException if authentication fails
    @throws          KeyError if response doesn't contain "Token" field
    @since           2019-09-03
    """
    endpoint = "/v1/auth/token"
    url = baseUrl + endpoint
    payload = "{\"UserName\": \"" + UserName + "\",\"Password\": \"" + Password + "\"}"
    headers = {
        'Content-Type': "application/json",
        'x-api-key': apiKey
    }
    response = requests.request("POST", url, data=payload, headers=headers)
    responseJson = response.json()
    token = responseJson["Token"]
    # refreshToken = responseJson["RefreshToken"]
    return token


# Define a function for interacting with the CAS API
def casapi(endpoint, apiKey, baseUrl, requestType="GET", payload="", contentJson=False, extraHeaders={}, urlParams={}):
    """
    Interact with various CAS API endpoints
    
    Generic function to make HTTP requests to the CAS API. Handles authentication,
    URL parameter construction, and header management for all API interactions.
    
    @param baseUrl       The root URL for the CAS API environment
    @param endpoint      The API endpoint path (see API documentation)
    @param apiKey        The user's API key for authentication
    @param requestType   The HTTP method to use (GET, POST, PUT, DELETE)
    @param payload       Optional request body content
    @param contentJson   Whether to set Content-Type as application/json
    @param extraHeaders  Additional HTTP headers as dictionary
    @param urlParams     URL query parameters as dictionary
    @return              requests.Response object from the API call
    @throws              requests.RequestException if API call fails
    @throws              Exception if authentication fails
    @since               2019-09-03
    @see                 https://api.liaisonedu.com/reference/swagger-ui/index.html
    """
    if len(urlParams) == 0:
        url = baseUrl + endpoint
    else:
        i = 1
        urlQueryStr = ""
        for urlParam, urlParamValue in urlParams.items():
            if i == 1:
                urlQueryPart = "?" + urlParam + "=" + str(urlParamValue)
            else:
                urlQueryPart = "&" + urlParam + "=" + str(urlParamValue)
            urlQueryStr += urlQueryPart
            i += 1
        url = baseUrl + endpoint + urlQueryStr
    token = auth(apiKey=apiKey, UserName=UserName, Password=Password)
    if contentJson:
        headers = {
            'Authorization': token,
            'x-api-key': apiKey,
            'Content-Type': "application/json"
        }
        headers.update(extraHeaders)
    else:
        headers = {
            'Authorization': token,
            'x-api-key': apiKey,
        }
        headers.update(extraHeaders)
    response = requests.request(requestType, url, data=payload, headers=headers)
    return response


# Define a function to express JSON strings as tables
def Tabular(listOfDicts):
    """
    Present lengthy JSON string in tabular format for ease of review
    
    Converts JSON response data into a formatted table for better readability
    during development and debugging. Handles both single objects and lists.
    
    @param listOfDicts   JSON response data as list of dictionaries or single dict
    @return              BeautifulTable object with formatted data
    @throws              Exception if data structure is unexpected
    @since               2019-09-03
    """
    try:
        headers = list(listOfDicts[0].keys())
        table = BeautifulTable()
        table.column_headers = headers

        for row in listOfDicts:
            normalized_row = [row.get(key, "") for key in headers]
            table.append_row(normalized_row)

        return table
    except:
        listOfDicts = [listOfDicts]
        table = BeautifulTable()
        table.column_headers = list(listOfDicts[0].keys())
        end = len(listOfDicts) - 1
        for i in range(0, end):
            table.insert_row(i, listOfDicts[i].values())
        return table


# Step 1: Choose the Application Form ID
# Application Form IDs
# Additionally, you’ll be given a list of ID numbers (called “Instance IDs”, “Form IDs”, or “Application Form IDs”)
# which represent the data sets to which you have access. A single ID represents a combination of a single CAS with
# a single admissions cycle year (e.g. “CASPA 2018-2019”).
# Store this list for future reference; you’ll need to send an Instance ID with every call to the CAS API.
#
# First, decide which CAS you want to retrieve data for.
# Each Application Form ID you were given when you gained access to the CAS API provides access to data for a
# specific CAS and admissions cycle year. You’ll include the Application Form ID for the CAS/cycle of interest
# in the URL of every subsequent call to the CAS API.
applicationFormId =   # enter chosen application form ID here
applicationFormId = str(applicationFormId)

# Step 2: List of Organizations
# Next, you’ll want to retrieve a list of the organizations you have access to.
# Remembering to include your API key and authorization token as headers, make a call to the “Get Organizations”
# endpoint (GET /v1/applicationForms/:applicationFormID/organizations
# https://api.liaisonedu.com/reference/index.htm#/operations/Organization/getOrganizations).
# The response to this call will include a list of all the organizations (schools/colleges/universities) within the
# chosen CAS that you have access to. In many cases, you’ll have access to just one organization –
# for example, in SOPHAS, you’ll likely just have access to your institution’s school of public health.
# In other CASs, however, your institution will likely have many organizations – for example, in GradCAS,
# your institution will likely have separate organizations for each college on campus. With this list,
# you can either choose an individual organization to retrieve data from, or you can loop through all
# available organizations retrieving all available data.
# The “id” field from each organization in the list is the “organizationId” used in the URL strings of subsequent calls.
endpoint = "/v1/applicationForms/" + applicationFormId + "/organizations"
response = casapi(baseUrl=baseUrl, endpoint=endpoint, apiKey=apiKey)
responseJson = response.json()
print(Tabular(responseJson))
organizationId =   # enter chosen organization ID here
organizationId = str(organizationId)

# Step 3: List of Programs
# You’ll need to retrieve lists of the programs offered by each organization. Make a call to the “Get Organization
# Programs” endpoint (GET /v1/applicationForms/:applicationFormID/organizations/:organizationID/programs
# https://api.liaisonedu.com/reference/index.htm#/operations/Program/getOrganizationPrograms).
# The response to this call will include a list of all the programs offered by the chosen organization in the
# chosen CAS. Again, your chosen organization may offer one or many programs. You can either choose an individual
# program to retrieve data from, or you can loop through all available programs retrieving all available data.
# The “id” field from each program in the list is the “programId” used in the URL string of subsequent calls.
endpoint = "/v1/applicationForms/" + applicationFormId + "/organizations/" + organizationId + "/programs"
response = casapi(baseUrl=baseUrl, endpoint=endpoint, apiKey=apiKey)
responseJson = response.json()
print(Tabular(responseJson))
programId =   # enter chosen program ID here
programId = str(programId)

# Step 4: Program Details
# For each individual program, you’ll want to include some details about the program with the application data.
# Retrieve program details by making a call to the “Get Program Info” endpoint
# (GET /v1/applicationForms/:applicationFormID/organizations/:organizationID/programs/:programId
# https://api.liaisonedu.com/reference/index.htm#/operations/Program/getProgramInfo).
# The response to this call will include details – such as campus, level, academic year,
# and delivery method – about the chosen program. Store these program details as an
# object so that you can include them with each application to the program.
endpoint = "/v1/applicationForms/" + applicationFormId + "/organizations/" + organizationId + "/programs/" + programId
response = casapi(baseUrl=baseUrl, endpoint=endpoint, apiKey=apiKey)
responseJson = response.json()
print(Tabular(responseJson))
programDetails = responseJson

# Step 5: Set save folder name to identify the CAS, Org, Program, and time of retrieval
saveFolder = "CASData-" + applicationFormId + "_" + organizationId + "_" + programId + "_" \
             + startTime.strftime("%Y-%m-%d_%H%M")
os.mkdir(saveDir + saveFolder)

# Step 6: List of Applications by Organization and Program
# Next, you’ll retrieve a list of all applications to each selected program offered by the chosen organization.
# Call the “Get Application Submissions by Program” endpoint
# (GET /v1/applicationForms/:applicationFormId/organizations/:organizationId/programs/:programId/applications
# https://api.liaisonedu.com/reference/index.htm#/operations/Application/getApplicationSubmissionsByProgram).
# The response to this call will be a list of all the applicants who have applied to the chosen program.
# You’ll want to loop through all of the applicants in the list and retrieve all application data available.
# The “applicationID” item will be used in the URL of a subsequent call to retrieve the full application data
# for individual applications.
#
# The calls that return lists of applications can be augmented to change the way the lists are delivered.
# There are two optional URL string parameters that will change which applications are returned
# •	URL string parameters
#   o	“fromDate” – date format: “yyyy-mm-dd”
#       	Use this URL string parameter to limit the applications returned to those that were updated on or after
#           the date indicated.
#   o	“toDate” – date format: “yyyy-mm-dd”
#       	Use this URL string parameter to limit the applications returned to those that were updated on or before
#           the date indicated.
# The URL string parameters should be passed to the function as a dictionary, like so:
# urlParams = {
#     'fromDate': "yyyy-mm-dd",
#     'toDate': "yyyy-mm-dd"
# }
endpoint = "/v1/applicationForms/" + applicationFormId + "/organizations/" + organizationId + "/programs/" \
           + programId + "/applications"
fromDate = datetime.strftime(datetime.now()-timedelta(days=1), '%Y-%m-%d')  # yesterday's date formatted 'yyyy-mm-dd'
# Don't use this params since data would be empty
# urlParams = {
#     'fromDate': fromDate    # retrieve only applications updated yesterday or later
#     # 'fromDate': "2019-08-01"  # retrieve only applications updated on or after a static date
#     ,'toDate': fromDate     # retrieve only applications updated yesterday or earlier
#     # ,'toDate': "2019-09-01"   # retrieve only applications updated on or before a static date
# }
urlParams = {}
response = casapi(baseUrl=baseUrl, endpoint=endpoint, apiKey=apiKey, urlParams=urlParams)
applicationList = response.json()

# Step 7: Retrieve Application Details
# With the list of applications, we’re ready to retrieve application data. For each application in the list,
# make a call to the “Get Application by Organization and Program” endpoint
# (GET /v1/applicationForms/:applicationFormId/organizations/:organizationId/programs/:programId/applications/:applicationId
# https://api.liaisonedu.com/reference/index.htm#/operations/Application/getApplicationByOrganizationAndProgram).
# The response to this call will include all available data pertaining to the selected application.
#
# The calls that return application details can be augmented to change the way the details are delivered.
# There are two optional URL string parameters and an optional header that will change the format of the data.
# •	URL string parameters
#   o	 “expand” – Available values: lookups, config, all
#       	This URL string parameter allows you to change the form of the data returned. The default form includes
#       numeric codes for all data points. For example, instead of returning the name of a country, the default might
#       return a numeric identifier for that country. The “lookups” option will replace those numeric identifiers with
#       values from the universal, CAS-wide lookup tables. The “config” option will replace those numeric identifiers
#       with values from the user-controlled, custom elements (either at the CAS level or at the organization or
#       program level). The “all” option will replace all numeric identifiers with values from the lookup tables or
#       custom values. The “all” option is the most recommended, as it eliminates the need for you to translate any
#       numeric identifiers.
#   o	“includeNulls” – Available values: true, false
#       	This URL string parameter allows you to force the response to include the same number of elements every
#       time. The default setting of “false” will hide elements for which the selected applicant has no data. For
#       example, if an applicant didn’t enter her middle name, the default payload for that applicant wouldn’t include
#       a middle name element. Setting “includeNulls=true” will return all elements in the data model whether or not
#       the selected applicant has data for each element. This setting is useful because it will guarantee a consistent
#       structure to the application data. That will help when parsing the JSON structure or if you are returning data
#       in a flat file format.
# •	Headers
#   o	“Accept”: “text/csv”
#       	This header allows you to cause the application details payload to be returned as a flat file in CSV format.
#       The default setting returns the payload as a JSON structure. This setting is useful if you intend to upload a
#       csv to your local system.
#   o	“X-API-CSV-COLUMN-SEPARATOR”:
#       Available values : COMMA, TAB, PIPE, SEMICOLON, Default value : COMMA
#       	This header is to be used only in combination with the “Accept:text/csv” header. This parameter allows you
#       to define the delimiter you want to use in your flat file. While the default delimiter is a comma, you can
#       choose tab, pipe, or semicolon delimiters. This is a handy feature that can accommodate the ingestion
#       requirements of a variety of on-campus systems.
# The URL string parameters should be passed to the function as a dictionary, like so:
# urlParams = {
#     'expand': "all"
#     ,'includeNulls': True
# }
# extraHeaders = {
#     'Accept': "text/csv"
#     ,'X-API-CSV-COLUMN-SEPARATOR': "TAB"
# }

applicationDetails = []
i = 0
for application in applicationList["applications"]:
    i += 1
    print(str(i) + "\t" + str(len(applicationList) - i) + " left\t\t" + str(application["applicationId"]) + "\t"
          + application["applicantFirstName"] + "\t" + application["applicantLastName"])
    applicationId = application["applicationId"]
    applicationId = str(applicationId)
    endpoint = "/v1/applicationForms/" + applicationFormId + "/organizations/" + organizationId + "/applications/" \
               + applicationId
    urlParams = {
        'expand': "all"
    }
    response = casapi(baseUrl=baseUrl, endpoint=endpoint, apiKey=apiKey, urlParams=urlParams)
    print(response.status_code)
    responseJson = response.json()
    for key, value in programDetails.items():
        responseJson["programDetails_" + key] = value
    applicationDetails.append(responseJson)
    time.sleep(1)

# Step 8: Load Data to Local Systems
# Once you’ve retrieved the application data for a single application, you’re ready to load it to your local systems.
# The power of the JSON payload is that you can programmatically transfer complex data objects from our database
# to yours. At this point, there are a number of possible approaches.
# (A)	One at a time, insert each raw application in its entirety into your database
#       (to be transformed and loaded to the appropriate tables after the initial insert).
# (B)	One at a time, parse each raw application for desired data elements and transform them to meet the
#       requirements of your database, then load the pared down and transformed application data to the relevant tables.
# (C)	Retrieve the details for all applications and store them together in an object, then insert that object with
#       all the raw application details to your database (to be transformed and loaded to the appropriate tables
#       after the initial insert).
# (D)	As you retrieve details for each application, parse the raw application for desired data elements and
#       transform them to meet the requirements of your database; group these pared down and transformed applications
#       into an object and load that object to the relevant tables.
#
# You are the expert in the nuances of loading data to your local databases, so you will know best which approach
# fits your situation. You will also know best how exactly to carry that out.