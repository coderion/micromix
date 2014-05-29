package io.fabric8.process.spring.boot.actuator.camel.rest;

public class FailingRestInterceptor implements RestInterceptor {

    @Override
    public boolean intercept(RestRequest request) {
        return false;
    }

}
