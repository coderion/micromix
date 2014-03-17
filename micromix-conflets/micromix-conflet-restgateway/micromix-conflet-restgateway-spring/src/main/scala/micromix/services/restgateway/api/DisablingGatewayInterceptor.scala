package micromix.services.restgateway.api

class DisablingGatewayInterceptor extends GatewayInterceptor {
  override def intercept(gatewayRequest: GatewayRequest) = false
}
