package io.fabric8.process.spring.boot.actuator.camel.rest;

import java.util.HashMap;
import java.util.Map;

public class RestRequest {

    private final Map<String, String> headers;

    private final String service;

    private final String operation;

    private final String[] parameters;

    public RestRequest(Map<String, String> headers, String service, String operation, String[] parameters) {
        this.headers = headers;
        this.service = service;
        this.operation = operation;
        this.parameters = parameters;
    }

    public RestRequest(String service, String operation, String... parameters) {
        this(new HashMap<String, String>(), service, operation, parameters);
    }

    public Map<String, String> headers() {
        return headers;
    }

    public String service() {
        return service;
    }

    public String operation() {
        return operation;
    }

    public String[] parameters() {
        return parameters;
    }

}
