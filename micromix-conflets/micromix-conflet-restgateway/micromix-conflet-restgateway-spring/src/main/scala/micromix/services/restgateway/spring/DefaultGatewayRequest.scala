package micromix.services.restgateway.spring

case class DefaultGatewayRequest(service: String, operation: String, parameters: Array[Any]) extends GatewayRequest
