package io.fabric8.process.spring.boot.actuator.camel.rest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RestPipelineTest extends Assert {

    RestRequest request = new RestRequest("service", "operation", null);

    RestInterceptor interceptor = mock(RestInterceptor.class);

    RestPipeline<Object> pipeline = new RestPipeline<Object>(interceptor) {
        @Override
        protected Object doDispatch(RestRequest restRequest, Object response) {
            return response;
        }
    };

    @Before
    public void before() {
        given(interceptor.intercept(request)).willReturn(true);
    }

    @Test
    public void shouldReturnGivenResponse() {
        // Given
        Object givenResponse = new Object();

        // When
        Object dispatchedResponse = pipeline.dispatch(request, givenResponse);

        // Then
        assertSame(givenResponse, dispatchedResponse);
    }

    @Test
    public void shouldAcceptRequest() {
        // When
        pipeline.dispatch(request, null);

        // Then
        verify(interceptor).intercept(request);
    }

    @Test(expected = RestInterceptorException.class)
    public void shouldRejectRequest() {
        // Given
        given(interceptor.intercept(request)).willReturn(false);

        // When
        pipeline.dispatch(request, null);
    }

}
