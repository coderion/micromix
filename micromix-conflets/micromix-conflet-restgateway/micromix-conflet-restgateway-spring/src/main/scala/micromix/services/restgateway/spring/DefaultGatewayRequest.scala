package micromix.services.restgateway.spring

import micromix.services.restgateway.api.GatewayRequest
import java.util.{Map => JMap}

case class DefaultGatewayRequest(headers: JMap[String, String], service: String, operation: String, parameters: Array[String]) extends GatewayRequest
