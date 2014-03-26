package micromix.services.restgateway.api

trait ApiExposer {

  def apiSchema(services: java.util.List[String]): String

}
