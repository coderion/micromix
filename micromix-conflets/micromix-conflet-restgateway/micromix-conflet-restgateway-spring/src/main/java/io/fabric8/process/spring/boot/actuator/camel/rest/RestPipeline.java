package io.fabric8.process.spring.boot.actuator.camel.rest;

public abstract class RestPipeline<RSP> {

    private final RestInterceptor restInterceptor;

    public RestPipeline(RestInterceptor restInterceptor) {
        this.restInterceptor = restInterceptor;
    }

    protected RSP dispatch(RestRequest restRequest, RSP rsp) {
        if (!restInterceptor.intercept(restRequest)) {
            throw new IllegalStateException("Cannot access gateway.");
        }
        return doDispatch(restRequest, rsp);
    }

    protected abstract RSP doDispatch(RestRequest restRequest, RSP rsp);

}
