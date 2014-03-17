package micromix.services.restgateway.api

abstract class InterceptedGatewayRequestDispatcher(gatewayInterceptor: GatewayInterceptor) extends GatewayRequestDispatcher {

  def dispatch(gatewayRequest: GatewayRequest) {
    if (!gatewayInterceptor.intercept(gatewayRequest)) {
      throw new IllegalAccessException("Cannot access gateway.")
    }
    doDispatch(gatewayRequest)
  }

  def doDispatch(gatewayRequest: GatewayRequest)

}
