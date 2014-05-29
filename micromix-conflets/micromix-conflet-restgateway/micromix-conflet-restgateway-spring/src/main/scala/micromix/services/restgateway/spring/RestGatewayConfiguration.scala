package micromix.services.restgateway.spring

import org.springframework.context.annotation.{Bean, Configuration}
import org.apache.camel.{CamelContext, RoutesBuilder, Exchange, Processor}
import org.springframework.beans.factory.annotation.{Value, Autowired}
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.netty.http.NettyHttpMessage
import org.apache.camel.model.dataformat.JsonLibrary
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationContext
import org.jboss.netty.handler.codec.http.HttpRequest
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping
import micromix.services.restgateway.api._
import micromix.conflet.restgateway.FixedTokenAuthGatewayInterceptor
import io.fabric8.process.spring.boot.actuator.camel.rest.{RestRequestMapper, RestPipeline, RestInterceptor, RestRequest}

@Configuration
class RestGatewayConfiguration {

  @Autowired(required = false)
  var gatewayInterceptor: RestInterceptor = new EnablingGatewayInterceptor

  @Bean
  def nettyGatewayEndpointRoute =
    new NettyGatewayEndpointRoute(gatewayInterceptor)

}

class NettyGatewayEndpointRoute(gatewayInterceptor: RestInterceptor) extends RestPipeline[Exchange](gatewayInterceptor) with RoutesBuilder {

  @Autowired
  private var applicationContext: ApplicationContext = _

  private val inputJsonMapper = new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_FINAL)

  @Autowired(required = false)
  var gatewayRequestMapper: RestRequestMapper[HttpRequest] = new NettyGatewayRequestMapper

  @Value("${micromix.services.restgateway.spring.netty.port:18080}")
  var port: Int = _

  def addRoutesToCamelContext(cc: CamelContext) {
    cc.addRoutes(new RouteBuilder() {
      override def configure() {
        onException(classOf[java.lang.Exception]).handled(true).process(new Processor() {
          override def process(exchange: Exchange) {
            val ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, classOf[java.lang.Exception])
            exchange.getIn.setBody(ex.getClass.getSimpleName + ": " + ex.getMessage)
          }
        }).marshal().json(JsonLibrary.Jackson)

        from("netty-http:http://0.0.0.0:" + port + "/api?matchOnUriPrefix=true").
          process(new Processor() {
          override def process(exchange: Exchange) {
            val request = exchange.getIn(classOf[NettyHttpMessage]).getHttpRequest
            val body = exchange.getIn.getBody(classOf[String])
            val x = gatewayRequestMapper.mapRequest(request)
            val bean = applicationContext.getBean(x.service).getClass
            val method = bean.getDeclaredMethods.find(_.getName == x.operation) match {
              case Some(m) => m
              case None => throw new IllegalArgumentException("No such method")
            }
            try {
              dispatch(x, exchange)
            } catch {
              case e: IllegalAccessException =>
                exchange.getIn.setHeader("ACL_EXCEPTION", true)
            }
            if (body.isEmpty) {
              exchange.getIn.setBody(x.parameters.zipWithIndex.map(p => cc.getTypeConverter.convertTo(method.getParameterTypes()(p._2), p._1)))
            } else {
              val parameterType = method.getParameterTypes()(0)
              exchange.getIn.setBody(x.parameters :+ inputJsonMapper.readValue(body, parameterType))
            }
            exchange.getIn.setHeader("bean", x.service)
            exchange.getIn.setHeader("method", x.operation)
            exchange.getIn.setHeader("Access-Control-Allow-Origin", "*")
            exchange.getIn.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, " + FixedTokenAuthGatewayInterceptor.tokenHeader)
          }
        }).
          choice().
          when(header("ACL_EXCEPTION").isEqualTo(true)).setBody().constant("ACCESS DENIED").endChoice().
          otherwise().recipientList().simple("bean:${headers.bean}?method=${headers.method}&multiParameterArray=true").
          marshal().json(JsonLibrary.Jackson).log("${body}").endChoice()
      }
    })
  }

  protected override def doDispatch(restRequest: RestRequest, rsp: Exchange): Exchange =
    rsp

}