git# PHP Sample CAS API client
The purpose of this repository is to provide an example client written in PHP to interact with the Liaison CAS API.

## Configuration
This sample repo requires you to create `.env` file at the root of this repository. You can copy the template from `/.env.example` found in this repo.  

## Dependencies
Dependencies need to be installed via composer. (https://getcomposer.org/)

## Files
* `\cas_api_client.php` - contains CAS API client that implements a few sample REST requests
* `\example.php` - an example of how to to use the CAS API client

## Output
When you execute the example script (`php example.php`) you should get a similar output as disaplyed below.

```
***** Authenticating and retrieving auth token *****
***** Authentication Succeeded *****
***** Creating a new organization *****
stdClass Object
(
    [organization] => stdClass Object
        (
            [id] => 12629
            [name] => NewTestOrg
            [sortIndex] => 1
            [uniqueIdentifier] => 1950459029904559601
            [orgCode] => SomeOrgCode3
            [createdDate] => 2020-02-20 16:44:46
            [updatedDate] => 2020-02-20 16:44:46
        )

)
***** Updating the organization *****
stdClass Object
(
    [organization] => stdClass Object
        (
            [id] => 12629
            [name] => UpdatedTestOrg
            [sortIndex] => 2
            [uniqueIdentifier] => 4569643314153156954
            [orgCode] => OrgCode4
            [createdDate] => 2020-02-20 16:45:01
            [updatedDate] => 2020-02-20 17:02:38
        )

)
***** Retrieve the list of all organizations *****
ORG ID: 11425
ORG Name: DemoOrg

ORG ID: 9846
ORG Name: Watertown University

ORG ID: 12629
ORG Name: NewTestOrg

***** Retrieve a specific organization *****
ORG ID: 12629
ORG Name: UpdatedTestOrg

stdClass Object
(
    [id] => 12629
    [name] => UpdatedTestOrg
    [sortIndex] => 2
    [uniqueIdentifier] => 4569643314153156954
    [orgCode] => OrgCode4
    [createdDate] => 2020-02-20 16:45:01
    [updatedDate] => 2020-02-20 17:02:38
)
***** Creating a new program *****
stdClass Object
(
    [program] => stdClass Object
        (
            [id] => 210128
            [name] => MyTestProgram
            [status] => draft
            [startDate] => 2019-03-22
            [deadline] => 2019-10-18
            [uniqueIdentifier] => 3828889264927610132
            [earlyDecisionEnable] => 1
            [academicYear] => 2019
            [type] => Masters
            [startTerm] => Fall
            [fee] => 0
            [city] => Watertown
            [state] => MA
            [zipCode] => 12345
            [createdDate] => 2020-02-20 16:59:43
            [updatedDate] => 2020-02-20 16:59:43
        )

)
***** Updating the program *****
stdClass Object
(
    [program] => stdClass Object
        (
            [id] => 210128
            [name] => MyUpdatedTestProgram
            [status] => draft
            [startDate] => 2020-03-22
            [deadline] => 2020-10-18
            [uniqueIdentifier] => 3828889264927610132
            [earlyDecisionEnable] => 1
            [academicYear] => 2020
            [type] => Masters
            [startTerm] => Spring
            [fee] => 0
            [city] => Poughkeepsie
            [state] => NY
            [zipCode] => 12601
            [createdDate] => 2020-02-20 16:59:43
            [updatedDate] => 2020-02-20 17:02:40
        )

)
***** Retrieve the list of programs for the organization *****
Program ID: 210128
Program Name: MyUpdatedTestProgram

***** Retrieve a specific program *****
Program ID: 210128
Program Name: MyUpdatedTestProgram

stdClass Object
(
    [id] => 210128
    [name] => MyUpdatedTestProgram
    [status] => draft
    [startDate] => 2020-03-22
    [deadline] => 2020-10-18
    [uniqueIdentifier] => 3828889264927610132
    [earlyDecisionEnable] => 1
    [academicYear] => 2020
    [type] => Masters
    [startTerm] => Spring
    [fee] => 0
    [city] => Poughkeepsie
    [state] => NY
    [zipCode] => 12601
    [createdDate] => 2020-02-20 16:59:43
    [updatedDate] => 2020-02-20 17:02:40
)
```
## Example of execution
### Preconditions:
- install php      (according to your OS)
- install composer (according to your OS)
### Execution
- `composer update` (downloads and updates php dependencies)
- `php example.php`

## CAS API Documentation Links
https://developer.liaisonedu.com/  
https://api.liaisonedu.com/reference/index.htm

## Contact
For questions or support regarding this PHP CAS API client, please contact:

**Jimmy Henson**  
Email: jhenson@liaisonedu.com