package micromix.services.restgateway.spring

trait GatewayRequest {

  def service: String

  def operation: String

  def parameters: Array[Any]

}