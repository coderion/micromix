package micromix.conflet.restgateway

import org.springframework.beans.factory.annotation.Value
import FixedTokenAuthGatewayInterceptor._
import io.fabric8.process.spring.boot.actuator.camel.rest.{RestRequest, RestInterceptor}

case class FixedTokenAuthGatewayInterceptor() extends RestInterceptor {

  // Members

  @Value("${micromix.conflet.restgateway.token.expected:secretMicroMixToken}")
  private[restgateway] var expectedToken: String = _

  // Constructors

  def this(token: String) = {
    this()
    expectedToken = token
  }

  // Overridden

  override def intercept(gatewayRequest: RestRequest): Boolean =
    gatewayRequest.headers.get(tokenHeader) == expectedToken

}

object FixedTokenAuthGatewayInterceptor {

  val tokenHeader = "micromix.conflet.restgateway.token"

  val plainApiHeader = "PLAIN_API"

  val defaultToken = "secretMicroMixToken"

}