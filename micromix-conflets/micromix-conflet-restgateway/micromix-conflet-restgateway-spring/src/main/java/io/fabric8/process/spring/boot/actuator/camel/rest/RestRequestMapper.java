package io.fabric8.process.spring.boot.actuator.camel.rest;

public interface RestRequestMapper<R> {

    RestRequest mapRequest(R request);

}
