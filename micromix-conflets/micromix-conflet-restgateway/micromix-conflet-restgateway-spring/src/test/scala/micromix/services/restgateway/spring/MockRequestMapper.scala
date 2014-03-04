package micromix.services.restgateway.spring

import micromix.services.restgateway.core.{GatewayRequest, GatewayRequestMapper}
import org.jboss.netty.handler.codec.http.HttpRequest

class MockRequestMapper extends GatewayRequestMapper[HttpRequest] {
  override def mapRequest(rawRequest: HttpRequest): GatewayRequest =
    null
}