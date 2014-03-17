package micromix.services.restgateway.api

import java.util.{Set => JSet}

class WhitelistAclGatewayInterceptor(allowedServices: JSet[String]) extends GatewayInterceptor {

  override def intercept(gatewayRequest: GatewayRequest) =
    allowedServices.contains(gatewayRequest.service)

}