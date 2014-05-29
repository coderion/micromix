package io.fabric8.process.spring.boot.actuator.camel.rest;

public class RestInterceptorException extends RuntimeException {

    public RestInterceptorException(String message) {
        super(message);
    }

}