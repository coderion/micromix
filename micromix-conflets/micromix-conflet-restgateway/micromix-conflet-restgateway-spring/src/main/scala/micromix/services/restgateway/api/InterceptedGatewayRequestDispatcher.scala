package micromix.services.restgateway.api

import io.fabric8.process.spring.boot.actuator.camel.rest.{RestRequest, RestInterceptor}

abstract class InterceptedGatewayRequestDispatcher(gatewayInterceptor: RestInterceptor) extends GatewayRequestDispatcher {

  def dispatch(gatewayRequest: RestRequest) {
    if (!gatewayInterceptor.intercept(gatewayRequest)) {
      throw new IllegalAccessException("Cannot access gateway.")
    }
    doDispatch(gatewayRequest)
  }

  def doDispatch(gatewayRequest: RestRequest)

}
