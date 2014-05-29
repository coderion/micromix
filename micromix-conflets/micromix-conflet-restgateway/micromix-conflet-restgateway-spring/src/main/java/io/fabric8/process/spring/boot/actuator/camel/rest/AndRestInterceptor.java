package io.fabric8.process.spring.boot.actuator.camel.rest;

public class AndRestInterceptor implements RestInterceptor {

    private final RestInterceptor[] interceptors;

    public AndRestInterceptor(RestInterceptor[] interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public boolean intercept(RestRequest request) {
        for (RestInterceptor interceptor : interceptors) {
            if (!interceptor.intercept(request)) {
                return false;
            }
        }
        return true;
    }

}