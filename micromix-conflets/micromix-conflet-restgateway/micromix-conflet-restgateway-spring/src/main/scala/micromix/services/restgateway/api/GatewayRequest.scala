package micromix.services.restgateway.api

trait GatewayRequest {

  def headers: java.util.Map[String, String]

  def service: String

  def operation: String

  def parameters: Array[String]

}
