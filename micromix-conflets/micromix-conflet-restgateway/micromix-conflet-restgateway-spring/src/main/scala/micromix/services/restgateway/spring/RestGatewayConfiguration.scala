package micromix.services.restgateway.spring

import io.fabric8.process.spring.boot.actuator.camel.rest._
import micromix.conflet.restgateway.FixedTokenAuthGatewayInterceptor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.netty.http.{DefaultNettySharedHttpServer, NettySharedHttpServerBootstrapConfiguration}
import org.apache.camel.model.dataformat.JsonLibrary
import org.apache.camel.{CamelContext, Exchange, Processor, RoutesBuilder, LoggingLevel}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.annotation.{Bean, Configuration}

import scala.collection.JavaConversions._

@Configuration
class RestGatewayConfiguration {

  @Value("${micromix.services.restgateway.spring.netty.port:18080}")
  var port: Int = _

  @Value("${micromix.api.mode:PRODUCTION}")
  var apiMode: String = _

  @Value("${micromix.services.restgateway.spring.netty.chunkedMaxContentLength:26214400}")
  var chunkedMaxContentLength: Int = _

  @Autowired(required = false)
  var gatewayInterceptor: RestInterceptor = new NopRestInterceptor

  @Bean
  def nettyGatewayEndpointRoute =
    new NettyGatewayEndpointRoute(pipelineProcessor, apiMode)

  @Bean
  def pipelineProcessor =
    new RestPipelineProcessor(gatewayInterceptor)

  @Bean(initMethod = "start", destroyMethod = "stop")
  def sharedNettyHttpServer = {
    val nettyConfig = new NettySharedHttpServerBootstrapConfiguration
    nettyConfig.setHost("0.0.0.0")
    nettyConfig.setPort(port)
    nettyConfig.setChunkedMaxContentLength(chunkedMaxContentLength)
    val netty = new DefaultNettySharedHttpServer()
    netty.setNettyServerBootstrapConfiguration(nettyConfig)
    netty
  }

}

class NettyGatewayEndpointRoute(restPipelineProcessor: RestPipelineProcessor, apiMode: String) extends RoutesBuilder {

  def addRoutesToCamelContext(cc: CamelContext) {
    cc.setUseBreadcrumb(false)
    cc.addRoutes(new RouteBuilder() {
      override def configure() {


        onException(classOf[java.lang.Exception]).handled(true).to("log:rest-error?level=ERROR").process(new Processor() {
          override def process(exchange: Exchange) {
            exchange.getIn.setHeader("Access-Control-Allow-Origin", "*")
            exchange.getIn.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, " +
              FixedTokenAuthGatewayInterceptor.tokenHeader + ", " + FixedTokenAuthGatewayInterceptor.plainApiHeader)
            val ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, classOf[java.lang.Exception])
            if (apiMode.equalsIgnoreCase("PRODUCTION")) {
              if (ex.getClass.getSimpleName.equalsIgnoreCase("DisabledException") ||
                ex.getClass.getSimpleName.equalsIgnoreCase("BadCredentialsException") ||
                ex.getClass.getSimpleName.equalsIgnoreCase("GeneralSecurityException") ||
                ex.getClass.getSimpleName.equalsIgnoreCase("LoginMismatchedException")) {
                exchange.getIn.setBody(ex.getClass.getSimpleName + ": " + ex.getMessage)
              } else {
                exchange.getIn.setBody("ApiException: API Error")
              }
            } else {
              exchange.getIn.setBody(ex.getClass.getSimpleName + ": " + ex.getMessage)
            }
            log.error("opis bledu: " + ex.getMessage)
          }
        }).marshal().json(JsonLibrary.Jackson)

        from("netty-http:http://0.0.0.0/api?matchOnUriPrefix=true&nettySharedHttpServer=#sharedNettyHttpServer").
          process(restPipelineProcessor).
          choice().
          when(header(Exchange.HTTP_METHOD).isEqualTo("OPTIONS")).setBody().constant("").endChoice().
          when(header("ACL_EXCEPTION").isEqualTo(true)).setBody().constant("ACCESS DENIED").endChoice().
          when(header("BINARY").isNotNull).recipientList().simple("bean:${headers.bean}?method=${headers.method}&multiParameterArray=true").setHeader("Content-Type", constant("application/octet-stream")).process(new Processor {
          override def process(exchange: Exchange): Unit = {
            Headers.headers().foreach(kv => exchange.getIn.setHeader(kv._1, kv._2))
          }
        }).endChoice().
          otherwise().recipientList().simple("bean:${headers.bean}?method=${headers.method}&multiParameterArray=true").
          marshal().json(JsonLibrary.Jackson).log(LoggingLevel.DEBUG,"${body}").process(new Processor {
          override def process(exchange: Exchange): Unit = {
            Headers.headers().foreach(kv => exchange.getIn.setHeader(kv._1, kv._2))
          }
        }).endChoice()
      }
    })
  }

}