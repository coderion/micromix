package micromix.services.restgateway.api

trait GatewayRequest {

  def service: String

  def operation: String

  def parameters: Array[String]

}
