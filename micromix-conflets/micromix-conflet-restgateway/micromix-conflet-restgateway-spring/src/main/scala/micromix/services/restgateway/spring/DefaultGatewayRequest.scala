package micromix.services.restgateway.spring

import micromix.services.restgateway.api.GatewayRequest

case class DefaultGatewayRequest(service: String, operation: String, parameters: Array[Any]) extends GatewayRequest
