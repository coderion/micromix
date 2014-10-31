package io.fabric8.process.spring.boot.actuator.camel.rest;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class RestRequest {

    private final Map<String, String> headers;

    private final String service;

    private final String operation;

    private final String[] parameters;

    private final SocketAddress socketAddress;

    public RestRequest(Map<String, String> headers, String service, String operation, SocketAddress socketAddress, String... parameters) {
        this.headers = headers;
        this.service = service;
        this.operation = operation;
        this.parameters = parameters;
        this.socketAddress = socketAddress;
    }

    public RestRequest(String service, String operation, SocketAddress socketAddress, String... parameters) {
        this(new HashMap<String, String>(), service, operation, socketAddress, parameters);
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

    public SocketAddress socketAddress() {
        return socketAddress;
    }

}