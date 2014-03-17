package micromix.services.restgateway.api

class EnablingGatewayInterceptor extends GatewayInterceptor {
  override def intercept(gatewayRequest: GatewayRequest) = true
}
