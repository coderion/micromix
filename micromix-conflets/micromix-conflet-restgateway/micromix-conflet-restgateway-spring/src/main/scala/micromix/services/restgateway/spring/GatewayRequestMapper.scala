package micromix.services.restgateway.spring

trait GatewayRequestMapper[T <: Any] {

  def mapRequest(rawRequest: T): GatewayRequest

}
