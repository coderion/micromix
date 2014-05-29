package io.fabric8.process.spring.boot.actuator.camel.rest;

public interface RestInterceptor {

    boolean intercept(RestRequest request);


}
