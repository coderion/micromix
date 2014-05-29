package micromix.services.restgateway.spring

import org.jboss.netty.handler.codec.http.HttpRequest
import io.fabric8.process.spring.boot.actuator.camel.rest.{RestRequestMapper, RestRequest}

class MockRequestMapper extends RestRequestMapper[HttpRequest] {
  override def mapRequest(rawRequest: HttpRequest): RestRequest =
    null
}