package micromix.services.restgateway.core

trait GatewayRequestMapper[T <: Any] {

  def mapRequest(rawRequest: T): GatewayRequest

}
