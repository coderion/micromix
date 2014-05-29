package io.fabric8.process.spring.boot.actuator.camel.rest;

public class WhiteListAccessRestInterceptor implements RestInterceptor {

    private final RestOperation[] restOperations;

    public WhiteListAccessRestInterceptor(RestOperation... restOperations) {
        this.restOperations = restOperations;
    }

    @Override
    public boolean intercept(RestRequest request) {
        for (RestOperation operation : restOperations) {
            if (request.service().equals(operation.service()) && request.operation().equals(operation.operation())) {
                return true;
            }
        }
        return false;
    }

}