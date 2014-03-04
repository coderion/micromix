package micromix.services.restgateway.core

trait GatewayRequest {

  def service: String

  def operation: String

  def parameters: Array[Any]

}
