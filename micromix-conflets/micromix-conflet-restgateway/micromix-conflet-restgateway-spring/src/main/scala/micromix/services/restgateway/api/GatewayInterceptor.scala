package micromix.services.restgateway.api

trait GatewayInterceptor {

  def intercept(gatewayRequest: GatewayRequest): Boolean

}
