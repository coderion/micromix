package io.fabric8.process.spring.boot.actuator.camel.rest;

public abstract class RestPipeline<RSP> {

    private final RestInterceptor restInterceptor;

    public RestPipeline(RestInterceptor restInterceptor) {
        this.restInterceptor = restInterceptor;
    }

    protected RSP dispatch(RestRequest request, RSP response) {
        if (!restInterceptor.intercept(request)) {
            throw new RestInterceptorException("Cannot access gateway.");
        }
        return doDispatch(request, response);
    }

    protected abstract RSP doDispatch(RestRequest restRequest, RSP rsp);

}
