package micromix.services.restgateway.api

import io.fabric8.process.spring.boot.actuator.camel.rest.{RestRequest, RestInterceptor}

class DisablingGatewayInterceptor extends RestInterceptor {
  override def intercept(gatewayRequest: RestRequest) = false
}
