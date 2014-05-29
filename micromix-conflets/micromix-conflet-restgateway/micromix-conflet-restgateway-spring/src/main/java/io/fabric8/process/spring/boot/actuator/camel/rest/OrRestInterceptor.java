package io.fabric8.process.spring.boot.actuator.camel.rest;

public class OrRestInterceptor implements RestInterceptor {

    private final RestInterceptor[] interceptors;

    public OrRestInterceptor(RestInterceptor... interceptors) {
        this.interceptors = interceptors;
    }

    public static OrRestInterceptor or(RestInterceptor... interceptors) {
        return new OrRestInterceptor(interceptors);
    }

    @Override
    public boolean intercept(RestRequest request) {
        for (RestInterceptor interceptor : interceptors) {
            if (interceptor.intercept(request)) {
                return true;
            }
        }
        return false;
    }

}