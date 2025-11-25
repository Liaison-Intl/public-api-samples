<?php

/**
 * CAS API Client for Liaison CAS API
 * 
 * This file contains the CasApiClient class that provides methods to interact
 * with the Liaison CAS API for managing organizations and programs.
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

namespace LiasionCasApi;

use GuzzleHttp;
use GuzzleHttp\Exception\RequestException;

class CasApiClient
{
    private $timeout = 120;
    private $user_name;
    private $password;
    private $application_form_id;
    private $auth_token = null;
    private $auth_refresh_token = null;
    private $http_client;
    private $headers = [];
    private $auth_headers = [];

    /**
     * @param string $user_name
     * @param string $password
     * @param string $api_key
     * @param string $base_uri
     * @param int $application_form_id
     */
    public function __construct($user_name, $password, $api_key, $base_uri, $application_form_id)
    {
        $this->user_name = $user_name;
        $this->password = $password;
        $this->application_form_id = $application_form_id;

        $this->headers['x-api-key'] = $api_key;
        $this->http_client = new GuzzleHttp\Client(['base_uri' => $base_uri, 'timeout' => $timeout ?? $this->timeout]);
    }

    /**
     * sends a http request
     *
     * @param string $method ('GET', 'POST', 'PUT', 'PATCH', 'DELETE')
     * @param string $url - realtive path
     * @return object - rquest response data
     */
    private function request($method, $url, $data = [])
    {
        $options = [
            'headers' => array_merge($this->headers, $this->auth_headers)
        ];

        if ($data) {
            $options['json'] = $data;
        }

        try {
            $response = $this->http_client->request($method, $url, $options);

            return json_decode($response->getBody());
        } catch (RequestException $e) {
            // handle error and report the user
            echo $e->getMessage();
        }
    }

    /**
     * create a auth token to be used in future API requests
     *
     * @return bool
     */
    public function generateToken()
    {
        $url = '/v1/auth/token';
        $data = ['password' => $this->password, 'userName' => $this->user_name];

        $response_body = $this->request('POST', $url, $data);

        if (isset($response_body->Token) && isset($response_body->RefreshToken)) {
            // save the token and refresh token from the reponse for later use
            $this->auth_token = $response_body->Token;
            $this->auth_refresh_token = $response_body->RefreshToken;

            // add the auth token to
            $this->auth_headers = array_merge(['Authorization' => $this->auth_token], $this->headers);

            return true;
        } else {
            return false;
        }
    }

    /**
     * retrieve the list of organizations or a specific organization
     *
     * @param int $org_id - omit to retrieve the full list of arganizations
     * @return object - rquest response data
     */
    public function getOrganizations($org_id = null)
    {
        $url = "applicationForms/{$this->application_form_id}/organizations";

        if ($org_id) {
            $url .= "/{$org_id}";
        }

        return $this->request('GET', $url);
    }

    /**
     * create a new organization
     *
     * @param array $data - associative array containing organization field value pairs
     * @return object - rquest response data
     */
    public function createOrganization($data)
    {
        $url = "applicationForms/{$this->application_form_id}/organizations";

        return $this->request('POST', $url, $data);
    }

    /**
     * update an organization
     * @param int $org_id
     * @param array $data - associative array containing organization field value pairs
     * @return object - rquest response data
     */
    public function updateOrganization($org_id, $data)
    {
        $url = "applicationForms/{$this->application_form_id}/organizations/{$org_id}";

        return $this->request('PUT', $url, $data);
    }

    /**
     * retrieve the list of all programs for an organization or a specific program
     *
     *
     * @param int $org_id
     * @param int $program_id - omit to get all programs
     * @return object - rquest response data
     */
    public function getPrograms($org_id, $program_id = null)
    {
        $url = "applicationForms/{$this->application_form_id}/organizations/{$org_id}/programs";

        if ($program_id) {
            $url .= "/{$program_id}";
        }

        return $this->request('GET', $url);
    }

    /**
     * create a new program
     *
     * @param int $org_id
     * @param array $data - associative array containing program field value pairs
     * @return object - rquest response data
     */
    public function createProgram($org_id, $data)
    {
        $url = "applicationForms/{$this->application_form_id}/organizations/{$org_id}/programs";

        return $this->request('POST', $url, $data);
    }

    /**
     * update an program
     *
     * @param int $org_id
     * @param int $program_id
     * @param array $data - associative array containing program field value pairs
     * @return object - rquest response data
     */
    public function updateProgram($org_id, $program_id, $data)
    {
        $url = "applicationForms/{$this->application_form_id}/organizations/{$org_id}/programs/{$program_id}";

        return $this->request('PUT', $url, $data);
    }
}
