package micromix.services.restgateway.api

import io.fabric8.process.spring.boot.actuator.camel.rest.RestRequest

trait GatewayRequestDispatcher {

  def dispatch(gatewayRequest: RestRequest)

}
