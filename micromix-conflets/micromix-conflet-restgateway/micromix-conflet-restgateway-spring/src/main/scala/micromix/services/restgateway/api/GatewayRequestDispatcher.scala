package micromix.services.restgateway.api

trait GatewayRequestDispatcher {

  def dispatch(gatewayRequest: GatewayRequest)

}
