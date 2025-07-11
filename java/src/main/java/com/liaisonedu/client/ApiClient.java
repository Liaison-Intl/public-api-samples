package com.liaisonedu.client;

import java.io.IOException;
import java.util.Map;

public interface ApiClient<Request, Params, Body> {

    Map<String, Object> getRequest(String url, Params params) throws IOException;

    Map<String, Object> getRequestCompressed(String url, Params params) throws IOException;

    Map<String, Object> postRequest(String url, Params params, Body body) throws IOException;

    Map<String, Object> postRequestCompressed(String url, Map<String, Object> params, Object parameters) throws IOException;

    Map<String, Object> executeRequest(Request request) throws IOException;
}
