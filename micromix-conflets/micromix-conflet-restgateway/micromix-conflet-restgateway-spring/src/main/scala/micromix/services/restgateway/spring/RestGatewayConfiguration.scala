package micromix.services.restgateway.spring

import org.springframework.context.annotation.{Bean, Configuration}
import org.apache.camel.{CamelContext, RoutesBuilder, Exchange, Processor}
import org.springframework.beans.factory.annotation.{Value, Autowired}
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.dataformat.JsonLibrary
import micromix.conflet.restgateway.FixedTokenAuthGatewayInterceptor
import io.fabric8.process.spring.boot.actuator.camel.rest._

@Configuration
class RestGatewayConfiguration {

  @Autowired(required = false)
  var gatewayInterceptor: RestInterceptor = new NopRestInterceptor

  @Bean
  def nettyGatewayEndpointRoute =
    new NettyGatewayEndpointRoute(pipelineProcessor)

  @Bean
  def pipelineProcessor =
    new RestPipelineProcessor(gatewayInterceptor)

}

class NettyGatewayEndpointRoute(restPipelineProcessor: RestPipelineProcessor) extends RoutesBuilder {

  @Value("${micromix.services.restgateway.spring.netty.port:18080}")
  var port: Int = _

  def addRoutesToCamelContext(cc: CamelContext) {
    cc.addRoutes(new RouteBuilder() {
      override def configure() {
        onException(classOf[java.lang.Exception]).handled(true).to("log:rest-error?level=ERROR").process(new Processor() {
          override def process(exchange: Exchange) {
            exchange.getIn.setHeader("Access-Control-Allow-Origin", "*")
            exchange.getIn.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, " + FixedTokenAuthGatewayInterceptor.tokenHeader)
            val ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, classOf[java.lang.Exception])
            exchange.getIn.setBody(ex.getClass.getSimpleName + ": " + ex.getMessage)
          }
        }).marshal().json(JsonLibrary.Jackson)

        from("netty-http:http://0.0.0.0:" + port + "/api?matchOnUriPrefix=true").
          process(restPipelineProcessor).
          choice().
          when(header("ACL_EXCEPTION").isEqualTo(true)).setBody().constant("ACCESS DENIED").endChoice().
          otherwise().recipientList().simple("bean:${headers.bean}?method=${headers.method}&multiParameterArray=true").
          marshal().json(JsonLibrary.Jackson).log("${body}").endChoice()
      }
    })
  }

}