package io.fabric8.process.spring.boot.actuator.camel.rest;

public class RestOperation {

    private final String service;

    private final String operation;

    public RestOperation(String service, String operation) {
        this.service = service;
        this.operation = operation;
    }


    public String service() {
        return service;
    }

    public String operation() {
        return operation;
    }

}