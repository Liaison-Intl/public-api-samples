# Title:    Retrieve Documents from CAS API
# Language: Python 3.7
# Author:   Greg Martin
# Date:     9/3/2019
# Contact:  gmartin@liaisonedu.com

# Prepare Environment
# Make sure you have all the necessary libraries to interact with the API and access target directories
import requests  # make http requests, including API calls
from beautifultable import BeautifulTable   # present tables in output to easily review API responses
import time  # pause in between file generation status checks
from datetime import datetime, timedelta   # retrieve timestamp information for unique identification of downloaded files
import os   # find local directories
from zipfile import ZipFile  # tool for interacting with zip files
import csv  # tool for writing indexes of PDFs in zip files

# Credentials
# Once you’ve been granted access, you’ll be given credentials for accessing the CAS API:
# API key, username, and password.
# Store these credentials in a secure location; you’ll need to use them every time you interact with the CAS API.
apiKey = ""
UserName = ""
Password = ""

# Root URLs
# The root URL to use for the CAS API can depend on which CAS and environment (production, prelaunch) you want to use.
# For production data, the default root URL is https://api.liaisonedu.com.
# Unless otherwise informed, this is the root URL to use.
# Contact Liaison customer service if you’re unsure of which root URL to use.
baseUrl = "https://api.liaisonedu.com"

# Local Save Directory
# Choose where you want to download documents to on your local machine
saveDir = ""

# Runtime Timestamp
# Set a timestamp to be use in filenames to more clearly identify the results of script operations
startTime = datetime.now()


# Authorization
# To successfully make any calls to the CAS API, you’ll need an authorization token.
# The authorization call is the first call you’ll make in any interaction with the CAS API.
# The token is valid for one hour, so you may need to reauthorize if your operation lasts a long time.
# To retrieve an authorization token, make a call to the “Sign In” endpoint
# (POST /v1/auth/token
# https://api.liaisonedu.com/reference/index.htm#/operations/Security/signIn).
# You’ll need to include two headers:
# Content-Type: "application/json"
# x-api-key: "{{your API key here}}"
# You’ll also need to include your credentials in the body of the request:
# {
# 	"UserName": "{{your username here}}"
# 	,"Password": "{{your password here}}"
# }
# The response to this call will include a “Token” element.
# This “Token”, along with your API key, must be included in the headers of all subsequent calls to the CAS API.
# Your calls will all have these two headers:
# x-api-key: "{{your API key here}}"
# Authorization: “{{your auth Token here}}”
def auth(apiKey, UserName, Password):
    """
        Authorize session for CAS API
        :param apiKey: user account's CAS API key
        :param UserName: user account's username
        :param Password: user account's password
        :return: Authorization Token to be used with all requests
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
def casapi(endpoint, apiKey, baseUrl="https://api.liaisonedu.com", requestType="GET", payload="", contentJson=False, extraHeaders={}, urlParams={}):
    """
        Interact with various CAS API endpoints
        :param baseUrl: the root URL for the environment you want to interact with (Defaults to prod: baseUrl="https://api.liaisonedu.com")
        :param endpoint: the URL indicating the desired endpoint (see https://api.liaisonedu.com/reference/index.htm)
        :param xapikey: the user's API key
        :param requestType: the HTTP request to send e.g. "GET" (see https://api.liaisonedu.com/reference/index.htm)
        :param payload: OPTIONAL the body of the HTTP request
        :param contentJson: OPTIONAL adds a header indicating that the body of the request is "application/json"
        :param extraHeaders: OPTIONAL additional headers for the request; send key-values as dict
        :param urlParams: OPTIONAL additional URL string query parameters; send key-values as dict
        :return: raw response from endpoint
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
    Present lengthy JSON string in tabular format for ease of review.
    :param listOfDicts: JSON string from http response; should be a list of dictionaries
    :return: tabular format of JSON string
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
        end = len(listOfDicts)
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
applicationFormId =    # enter chosen application form ID here
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
organizationId =    # enter chosen organization ID here
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
programId =       # enter chosen program ID here
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
saveFolder = "CASDocuments-" + applicationFormId + "_" + organizationId + "_" + programId + "_" + startTime.strftime("%Y-%m-%d_%H%M")
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
#       	Use this URL string parameter to limit the applications returned to those that were updated on or after the date indicated.
#   o	“toDate” – date format: “yyyy-mm-dd”
#       	Use this URL string parameter to limit the applications returned to those that were updated on or before the date indicated.
# The URL string parameters should be passed to the function as a dictionary, like so:
# urlParams = {
#     'fromDate': "yyyy-mm-dd",
#     'toDate': "yyyy-mm-dd"
# }
endpoint = "/v1/applicationForms/" + applicationFormId + "/organizations/" + organizationId + "/programs/" + programId + "/applications"
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
#       numeric codes for all data points. For example, instead of returning the name of a country, the default
#       might return a numeric identifier for that country. The “lookups” option will replace those numeric
#       identifiers with values from the universal, CAS-wide lookup tables. The “config” option will replace those
#       numeric identifiers with values from the user-controlled, custom elements (either at the CAS level or at
#       the organization or program level). The “all” option will replace all numeric identifiers with values from
#       the lookup tables or custom values. The “all” option is the most recommended, as it eliminates the need for
#       you to translate any numeric identifiers.
# The URL string parameters should be passed to the function as a dictionary, like so:
# urlParams = {
#     'expand': "all",
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

# Step 8: List of Documents from Application Details
# Now that we have collected all of the details from the applications we’re interested in, we need to search these
# details to find available documents. There are 7 document types made available through the CAS API: college
# transcripts, official foreign transcript evaluations (WES, ECE), App Gateway applicant-uploaded documents
# (supplemental documents uploaded after application submission), letters of recommendation (CAS-level and
# program-level), CAS application applicant-uploaded documents (CAS-level and program-level). The presence of
# any of these documents and the information needed to retrieve them is included in the application details.
# Loop through all applications collected and pull out information for each document type.
#
# Documents and their locations in the standard CAS API application details JSON structure:
# 1) academicHistory/transcriptEntry/transcripts/.
# 2) academicHistory/transcriptEntry/vendorTranscriptEvaluations/.
# 3) appGateway/documentUpload/gatewayAttachments/.
# 4) programMaterials/evaluations/evaluationResponses/.
# 5) programMaterials/supplementalAttachments/.
# 6) supportingInfo/documents/attachments/.
# 7) supportingInfo/evaluations/evaluationResponses/.
possibleDocuments = []
i = 0
for applicationDetail in applicationDetails:
    i += 1
    # if i > 5:
    #     break
    print(str(i) + "\t" + str(len(applicationDetails) - i) + " left\t\t")# + str(applicationDetail["applicationId"]))
    # skip any incomplete responses
    if "applicationId" not in applicationDetail.keys():
        continue
    elif "biographicInfo" not in applicationDetail["personalInfo"].keys():
        continue
    elif "profile" not in applicationDetail["personalInfo"]["biographicInfo"].keys():
        continue
    applicationId = applicationDetail["applicationId"]
    casId = applicationDetail["casApplicantId"]
    firstName = applicationDetail["personalInfo"]["biographicInfo"]["profile"]["firstName"]
    lastName = applicationDetail["personalInfo"]["biographicInfo"]["profile"]["lastName"]
    programName = applicationDetail["programDetails_name"]
    programWebadmitName = applicationDetail["programDetails_webadmitName"]
    # 1) academicHistory/transcriptEntry/transcripts/.
    if "academicHistory" in applicationDetail.keys():
        academicHistory = applicationDetail["academicHistory"]
        if "transcriptEntry" in academicHistory.keys():
            transcriptEntry = academicHistory["transcriptEntry"]
            if "transcripts" in transcriptEntry.keys():
                transcripts = transcriptEntry["transcripts"]
                for transcript in transcripts:
                    transcript["applicationId"] = applicationId
                    transcript["casId"] = casId
                    transcript["firstName"] = firstName
                    transcript["lastName"] = lastName
                    transcript["programName"] = programName
                    transcript["programWebadmitName"] = programWebadmitName
                    possibleDocuments.append(transcript)
    # 2) academicHistory/transcriptEntry/vendorTranscriptEvaluations/.
    if "academicHistory" in applicationDetail.keys():
        academicHistory = applicationDetail["academicHistory"]
        if "transcriptEntry" in academicHistory.keys():
            transcriptEntry = academicHistory["transcriptEntry"]
            if "vendorTranscriptEvaluations" in transcriptEntry.keys():
                vendorTranscriptEvaluations = transcriptEntry["vendorTranscriptEvaluations"]
                for vendorTranscriptEvaluation in vendorTranscriptEvaluations:
                    vendorTranscriptEvaluation["applicationId"] = applicationId
                    vendorTranscriptEvaluation["casId"] = casId
                    vendorTranscriptEvaluation["firstName"] = firstName
                    vendorTranscriptEvaluation["lastName"] = lastName
                    vendorTranscriptEvaluation["programName"] = programName
                    vendorTranscriptEvaluation["programWebadmitName"] = programWebadmitName
                    possibleDocuments.append(vendorTranscriptEvaluation)
    # 3) appGateway/documentUpload/gatewayAttachments/.
    if "appGateway" in applicationDetail.keys():
        appGateway = applicationDetail["appGateway"]
        if "documentUpload" in appGateway.keys():
            documentUpload = appGateway["documentUpload"]
            if "gatewayAttachments" in documentUpload.keys():
                gatewayAttachments = documentUpload["gatewayAttachments"]
                for gatewayAttachment in gatewayAttachments:
                    gatewayAttachment["applicationId"] = applicationId
                    gatewayAttachment["casId"] = casId
                    gatewayAttachment["firstName"] = firstName
                    gatewayAttachment["lastName"] = lastName
                    gatewayAttachment["programName"] = programName
                    gatewayAttachment["programWebadmitName"] = programWebadmitName
                    possibleDocuments.append(gatewayAttachment)
    # 4) programMaterials/evaluations/evaluationResponses/.
    if "programMaterials" in applicationDetail.keys():
        programMaterials = applicationDetail["programMaterials"]
        if "evaluations" in programMaterials.keys():
            evaluations = programMaterials["evaluations"]
            if "evaluationResponses" in evaluations.keys():
                evaluationResponses = evaluations["evaluationResponses"]
                for evaluationResponse in evaluationResponses:
                    evaluationResponse["applicationId"] = applicationId
                    evaluationResponse["casId"] = casId
                    evaluationResponse["firstName"] = firstName
                    evaluationResponse["lastName"] = lastName
                    evaluationResponse["programName"] = programName
                    evaluationResponse["programWebadmitName"] = programWebadmitName
                    possibleDocuments.append(evaluationResponse)
    # 5) programMaterials/supplementalAttachments/.
    if "programMaterials" in applicationDetail.keys():
        programMaterials = applicationDetail["programMaterials"]
        if "supplementalAttachments" in programMaterials.keys():
            supplementalAttachments = programMaterials["supplementalAttachments"]
            for supplementalAttachment in supplementalAttachments:
                supplementalAttachment["applicationId"] = applicationId
                supplementalAttachment["casId"] = casId
                supplementalAttachment["firstName"] = firstName
                supplementalAttachment["lastName"] = lastName
                supplementalAttachment["programName"] = programName
                supplementalAttachment["programWebadmitName"] = programWebadmitName
                possibleDocuments.append(supplementalAttachment)
    # 6) supportingInfo/documents/attachments/.
    if "supportingInfo" in applicationDetail.keys():
        supportingInfo = applicationDetail["supportingInfo"]
        if "documents" in supportingInfo.keys():
            documents = supportingInfo["documents"]
            if "attachments" in documents.keys():
                attachments = documents["attachments"]
                for attachment in attachments:
                    attachment["applicationId"] = applicationId
                    attachment["casId"] = casId
                    attachment["firstName"] = firstName
                    attachment["lastName"] = lastName
                    attachment["programName"] = programName
                    attachment["programWebadmitName"] = programWebadmitName
                    possibleDocuments.append(attachment)
    # 7) supportingInfo/evaluations/evaluationResponses/.
    if "supportingInfo" in applicationDetail.keys():
        supportingInfo = applicationDetail["supportingInfo"]
        if "evaluations" in supportingInfo.keys():
            evaluations = supportingInfo["evaluations"]
            if "evaluationResponses" in evaluations.keys():
                evaluationResponses = evaluations["evaluationResponses"]
                for evaluationResponse in evaluationResponses:
                    evaluationResponse["applicationId"] = applicationId
                    evaluationResponse["casId"] = casId
                    evaluationResponse["firstName"] = firstName
                    evaluationResponse["lastName"] = lastName
                    evaluationResponse["programName"] = programName
                    evaluationResponse["programWebadmitName"] = programWebadmitName
                    possibleDocuments.append(evaluationResponse)

# Step 9: Find Documents that have been Received
# With this new list of documents, we’ll now proceed to identify which are ready for download.
# Some document types will show up in the application details before a document is available.
# For example, letters of recommendation get attached to an application when the request is made to the recommender
# and can exist before the letter is actually submitted. Each document type has slightly different metadata.
# Loop through each possible document and determine if it is ready for download. Store the information for the
# documents that are ready to download in a new list.
receivedDocuments = []
i = 0
for document in possibleDocuments:
    i += 1
    # if i > 5:
    #     break
    print(str(i) + "\t" + str(len(possibleDocuments) - i) + " left\t\t" + str(document["id"]))
    # 1) academicHistory/transcriptEntry/transcripts/.
    if "receivedStatus" in document.keys():
        if document["receivedStatus"] == True:
            receivedDocuments.append(document)
    # 2) academicHistory/transcriptEntry/vendorTranscriptEvaluations/.
    elif "receivedDate" in document.keys():
        if document["receivedDate"]:
            receivedDocuments.append(document)
    # 3) appGateway/documentUpload/gatewayAttachments/.
    # 5) programMaterials/supplementalAttachments/.
    # 6) supportingInfo/documents/attachments/.
    elif "createdDate" in document.keys():
        if document["createdDate"]:
            receivedDocuments.append(document)
    # 4) programMaterials/evaluations/evaluationResponses/.
    # 7) supportingInfo/evaluations/evaluationResponses/.
    elif "responseDate" in document.keys():
        if document["responseDate"]:
            receivedDocuments.append(document)

# Step 10: Retrieve Documents
# Now that we’ve prepared a list of documents ready for download, it’s time to retrieve them.
# Loop through the list of available documents and make a call to the “Get File” endpoint
# (GET /v1/applicationForms/{applicationFormId}/files/{fileId}
# https://api.liaisonedu.com/reference/index.htm#/operations/File/getFile) for each one.
#
# At this point, it’s necessary to start thinking about how the documents are going to be loaded to your local systems.
# The upload approach will determine the download approach. For example, if your local system prefers to upload PDFs as
# a zip file, you’ll want to put all PDFs downloaded from the CAS API in this session into a single directory so that
# you can zip it up when done downloading. If, on the other hand, your local system has a tool for uploading individual
# PDFs that you plan to make use of programmatically, then you’ll want to deliver each document to that tool as it’s
# downloaded. As a final example, your local systems might require manual PDF uploads, in which case, you’ll probably
# want to organize the downloaded PDFs in a way that’s convenient for the people who will be processing them.
i = 0
for receivedDocument in receivedDocuments:
    i += 1
    # if i > 5:
    #     break
    print(str(i) + "\t" + str(len(receivedDocuments) - i) + " left\t\t" + str(receivedDocument["id"]))
    if "docType" in receivedDocument.keys():
        docType = receivedDocument["docType"]
        fileId = receivedDocument["id"]
        fileId = str(fileId)
    else:
        continue
    endpoint = "/v1/applicationForms/" + applicationFormId + "/files/" + fileId
    urlParams = {'docType': docType}
    response = casapi(baseUrl=baseUrl, endpoint=endpoint, apiKey=apiKey, urlParams=urlParams)
    print(response.status_code)
    # print(response.headers)
    if response.status_code != 200:
        continue
    # responseJson = response.json()
    filename = str(receivedDocument["applicationId"]) + "_" + str(receivedDocument["casId"]) + "_" \
               + receivedDocument["firstName"] + "_" + receivedDocument["lastName"] + "_" \
               + fileId + "_" + docType + ".pdf"
    open(saveDir + "/" + saveFolder + "/" + filename, 'wb').write(response.content)
    print(saveDir + "/" + saveFolder + "/" + filename)
    time.sleep(1)

# Step 11: Create an Index File Listing All PDFs in Zip File
# [ONLY IF NECESSARY FOR UPLOAD TO LOCAL SYSTEMS]
# Many SIS and CRM systems have tools for loading PDFs in bulk. While some of these tools can ingest zip directories
# full of PDF files and use the PDF filenames for mapping the documents, other tools require the zip directory to
# include an index file listing all of the PDFs present (Document Import Processor format). WebAdMIT’s PDF zip files
# don’t include an index file. If your system requires an index file for bulk PDF upload, you’ll have to create that
# outside of WebAdMIT. Some systems don’t have tools for loading images in bulk, so you’ll have to process each
# PDF individually.
newZip = saveDir + saveFolder
pdfIndexes = []
for pdf in os.listdir(newZip):
    pdfIndex = {}
    pdfNameElem = pdf.replace(".pdf", "").split("_")
    pdfIndex["applicationId"] = pdfNameElem[0]
    pdfIndex["casId"] = pdfNameElem[1]
    pdfIndex["firstName"] = pdfNameElem[2]
    pdfIndex["lastName"] = pdfNameElem[3]
    pdfIndex["fileId"] = pdfNameElem[4]
    pdfIndex["docType"] = pdfNameElem[5]
    pdfIndexes.append(pdfIndex)
print(Tabular(pdfIndexes))
# Write the filenames to an index text file
indexFilename = newZip + "/Index.txt"   # Consider what filename your local systems will look for in an index file
with open(indexFilename, 'w') as indexFile:
    # dictWriter = csv.DictWriter(indexFile, [key for key in pdfIndexes[0].keys()], delimiter="\t")
    dictWriter = csv.DictWriter(indexFile, pdfIndexes[0].keys(), delimiter="\t")
    dictWriter.writeheader()
    dictWriter.writerows(pdfIndexes)
# Zip up the new directory with the PDFs and index file
newZipFilename = newZip + "_INDEXED.zip"
with ZipFile(newZipFilename, mode="x") as newZipFile:
    for file in os.scandir(newZip):
        newZipFile.write(file, file.name)
    newZipFile.close()
# Clean up by removing the new directory used to build the new zip file
for file in os.scandir(newZip):
    os.remove(file)
os.rmdir(newZip)

# Step 12: Load Documents to Local Systems
# You are the expert on your local systems and business processes.
# Use your understanding of how PDFs are loaded to local systems and the business need
# for transferring PDFs to local systems to craft an approach that meets your institution’s needs.

endTime = datetime.now()
print("\nElapsed time\t\t" + str(endTime - startTime))
