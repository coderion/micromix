package micromix.services.restgateway.spring

import org.jboss.netty.handler.codec.http.HttpRequest
import micromix.services.restgateway.core.{GatewayRequest, GatewayRequestMapper}

class NettyGatewayRequestMapper extends GatewayRequestMapper[HttpRequest] {

  override def mapRequest(request: HttpRequest): GatewayRequest = {
    val uriFragments = request.getUri.split("/")
    val params: Array[Any] = uriFragments.slice(4, uriFragments.length).map(_.toInt)
    DefaultGatewayRequest(uriFragments(2), uriFragments(3), params)
  }

}
