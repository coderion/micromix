package micromix.services.restgateway.spring

import org.jboss.netty.handler.codec.http.HttpRequest
import micromix.services.restgateway.api.GatewayRequestMapper
import io.fabric8.process.spring.boot.actuator.camel.rest.RestRequest

class MockRequestMapper extends GatewayRequestMapper[HttpRequest] {
  override def mapRequest(rawRequest: HttpRequest): RestRequest =
    null
}