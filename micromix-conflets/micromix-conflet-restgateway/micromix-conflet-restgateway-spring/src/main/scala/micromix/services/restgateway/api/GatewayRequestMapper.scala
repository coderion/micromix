package micromix.services.restgateway.api

import io.fabric8.process.spring.boot.actuator.camel.rest.RestRequest

trait GatewayRequestMapper[T <: Any] {

  def mapRequest(rawRequest: T): RestRequest

}
