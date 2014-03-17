package micromix.services.restgateway.spring

import org.jboss.netty.handler.codec.http.HttpRequest
import micromix.services.restgateway.api.{GatewayRequestMapper, GatewayRequest}

class MockRequestMapper extends GatewayRequestMapper[HttpRequest] {
  override def mapRequest(rawRequest: HttpRequest): GatewayRequest =
    null
}