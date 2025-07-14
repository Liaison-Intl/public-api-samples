<?php

/**
 * Example usage of CAS API Client
 * 
 * This file demonstrates how to use the CasApiClient class to interact
 * with the Liaison CAS API. It includes examples of authentication,
 * creating/updating organizations and programs, and retrieving data.
 *
 * @author     Liaison International
 * @copyright  Copyright (c) 2020 Liaison International
 * @license    MIT License
 * @version    1.1.0
 * @created    2020-02-20
 * @updated    2024-07-14
 * 
 * @see        https://developer.liaisonedu.com/
 * @see        https://api.liaisonedu.com/reference/index.html
 */

require __DIR__ . '/vendor/autoload.php';
require __DIR__ . '/cas_api_client.php';

use Dotenv\Dotenv;
use LiasionCasApi\CasApiClient;

// load environment variables
$dotenv = Dotenv::createMutable(__DIR__);
$dotenv->load();

// instantiate the CAS API client
$cas_api_client = new CasApiClient(
    $_ENV['CAS_API_USERNAME'] ?? '',
    $_ENV['CAS_API_PASSWORD'] ?? '',
    $_ENV['CAS_API_KEY'] ?? '',
    $_ENV['CAS_API_BASE_URI'] ?? '',
    $_ENV['CAS_API_APPLICATION_FORM_ID'] ?? ''
);

// authenticate and retrieve auth tokens to use in future requests
echo "***** Authenticating and retrieving auth token *****".PHP_EOL;
$authenticated = $cas_api_client->generateToken();

if (!$authenticated) {
    die('Authentication Failed');
}
echo "***** Authentication Succeeded *****".PHP_EOL;


// create a new organization
echo "***** Creating a new organization *****".PHP_EOL;
$new_org_data = [
    "name"          => "NewTestOrg",
    "orgCode"       => "SomeOrgCode3",
    "sortIndex"     => 1
];
// NOTE: this request will fail if a organization with the same name already exists
$new_org_response = $cas_api_client->createOrganization($new_org_data);
print_r($new_org_response);
$new_org = $new_org_response->organization;


// update the organization
echo "***** Updating the organization *****".PHP_EOL;
$update_org_data = [
    "name"          => "UpdatedTestOrg",
    "orgCode"       => "OrgCode4",
    "sortIndex"     => 2
];
$updated_org_response = $cas_api_client->updateOrganization($new_org->id, $update_org_data);
print_r($updated_org_response);


// get the list of all organizations
echo "***** Retrieve the list of all organizations *****".PHP_EOL;
$orgs = $cas_api_client->getOrganizations();

foreach ($orgs as $org) {
    echo "ORG ID: ". $org->id. PHP_EOL;
    echo "ORG Name: ". $org->name.PHP_EOL.PHP_EOL;
}


// get a specific organization
echo "***** Retrieve a specific organization *****".PHP_EOL;
$org = $cas_api_client->getOrganizations($new_org->id);
echo "ORG ID: ". $org->id. PHP_EOL;
echo "ORG Name: ". $org->name.PHP_EOL.PHP_EOL;
print_r($org);


// create a new program
echo "***** Creating a new program *****".PHP_EOL;
$new_program_data = [
    "name"                  => "MyTestProgram",
    "status"                => "draft",
    "startDate"             => "2019-03-22",
    "startTerm"             => "Fall",
    "earlyDecisionEnable"   => true,
    "deadline"              => "2019-10-18",
    "academicYear"          => "2019",
    "type"                  => "Masters",
    "fee"                   => 0,
    "city"                  => "Watertown",
    "state"                 => "MA",
    "zipCode"               => "12345"
];
// NOTE: this request will fail if a program with the same name already exists
$new_program_response = $cas_api_client->createProgram($new_org->id, $new_program_data);
print_r($new_program_response);
$new_program = $new_program_response->program;


// update the program
echo "***** Updating the program *****".PHP_EOL;
$update_program_data = [
    "name"                  => "MyUpdatedTestProgram",
    "status"                => "draft",
    "startDate"             => "2020-03-22",
    "startTerm"             => "Spring",
    "earlyDecisionEnable"   => true,
    "deadline"              => "2020-10-18",
    "academicYear"          => "2020",
    "type"                  => "Masters",
    "fee"                   => 0,
    "city"                  => "Poughkeepsie",
    "state"                 => "NY",
    "zipCode"               => "12601"
];
$updated_program_response = $cas_api_client->updateProgram($new_org->id, $new_program->id, $update_program_data);
print_r($updated_program_response);


// get the list of programs for the specified org
echo "***** Retrieve the list of programs for the organization *****".PHP_EOL;
$programs = $cas_api_client->getPrograms($new_org->id);
foreach ($programs as $program) {
    echo "Program ID: ". $program->id. PHP_EOL;
    echo "Program Name: ". $program->name.PHP_EOL.PHP_EOL;
}


// get a specific program
echo "***** Retrieve a specific program *****".PHP_EOL;
$program = $cas_api_client->getPrograms($new_org->id, $new_program->id);
echo "Program ID: ". $program->id. PHP_EOL;
echo "Program Name: ". $program->name.PHP_EOL.PHP_EOL;
print_r($program);

echo PHP_EOL.PHP_EOL;
