package micromix.services.restgateway.api

trait GatewayRequestMapper[T <: Any] {

  def mapRequest(rawRequest: T): GatewayRequest

}
