package micromix.services.restgateway.spring

import org.jboss.netty.handler.codec.http.HttpRequest
import micromix.services.restgateway.api.{GatewayRequestMapper, GatewayRequest}

import scala.collection.JavaConversions._

class NettyGatewayRequestMapper extends GatewayRequestMapper[HttpRequest] {

  override def mapRequest(request: HttpRequest): GatewayRequest = {
    val headers = request.headers.iterator.foldLeft(Map[String, String]()) {
      (headersMap, incomingHeader) => headersMap + (incomingHeader.getKey -> incomingHeader.getValue)
    }
    val uriFragments = request.getUri.split("/")
    val service = uriFragments(2)
    val operation = uriFragments(3)
    val params = uriFragments.slice(4, uriFragments.length)
    DefaultGatewayRequest(headers, service, operation, params)
  }

}
