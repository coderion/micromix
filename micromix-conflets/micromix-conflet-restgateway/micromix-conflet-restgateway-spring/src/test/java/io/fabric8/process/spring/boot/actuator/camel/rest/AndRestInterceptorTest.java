package io.fabric8.process.spring.boot.actuator.camel.rest;

import org.junit.Assert;
import org.junit.Test;

public class AndRestInterceptorTest extends Assert {

    RestRequest restRequest = new RestRequest("service", "operation", null);

    @Test
    public void shouldBePositive() {
        RestInterceptor positiveAnd = new AndRestInterceptor(new NopRestInterceptor(), new NopRestInterceptor());
        assertTrue(positiveAnd.intercept(restRequest));
    }

    @Test
    public void shouldBeNegative() {
        RestInterceptor negativeAnd = new AndRestInterceptor(new NopRestInterceptor(), new FailingRestInterceptor());
        assertFalse(negativeAnd.intercept(restRequest));
    }

}