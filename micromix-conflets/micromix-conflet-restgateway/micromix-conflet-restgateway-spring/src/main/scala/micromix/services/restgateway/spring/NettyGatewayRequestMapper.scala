package micromix.services.restgateway.spring

import org.jboss.netty.handler.codec.http.HttpRequest
import micromix.services.restgateway.api.GatewayRequestMapper

import scala.collection.JavaConversions._
import io.fabric8.process.spring.boot.actuator.camel.rest.RestRequest

class NettyGatewayRequestMapper extends GatewayRequestMapper[HttpRequest] {

  override def mapRequest(request: HttpRequest): RestRequest = {
    val headers = request.headers.iterator.foldLeft(Map[String, String]()) {
      (headersMap, incomingHeader) => headersMap + (incomingHeader.getKey -> incomingHeader.getValue)
    }
    val uriFragments = request.getUri.split("/")
    val service = uriFragments(2)
    val operation = uriFragments(3)
    val params = uriFragments.slice(4, uriFragments.length)
    new RestRequest(headers, service, operation, params)
  }

}
