package micromix.services.restgateway.spring

import org.springframework.context.annotation.{Bean, Configuration}
import org.apache.camel.{CamelContext, RoutesBuilder, Exchange, Processor}
import org.springframework.beans.factory.annotation.{Value, Autowired}
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.netty.http.NettyHttpMessage
import org.apache.camel.model.dataformat.JsonLibrary
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationContext
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Method
import org.springframework.util.ReflectionUtils.MethodCallback
import org.jboss.netty.handler.codec.http.HttpRequest
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping
import micromix.services.restgateway.api._

@Configuration
class RestGatewayConfiguration {

  @Autowired(required = false)
  var gatewayInterceptor: GatewayInterceptor = new EnablingGatewayInterceptor

  @Bean
  def nettyGatewayEndpointRoute =
    new NettyGatewayEndpointRoute(gatewayInterceptor)

}

class NettyGatewayEndpointRoute(gatewayInterceptor: GatewayInterceptor) extends InterceptedGatewayRequestDispatcher(gatewayInterceptor) with RoutesBuilder {

  @Autowired
  private var applicationContext: ApplicationContext = _

  private val inputJsonMapper = new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_FINAL)

  @Autowired(required = false)
  var gatewayRequestMapper: GatewayRequestMapper[HttpRequest] = new NettyGatewayRequestMapper

  @Value("${micromix.services.restgateway.spring.netty.port:18080}")
  var port: Int = _

  def addRoutesToCamelContext(cc: CamelContext) {
    cc.addRoutes(new RouteBuilder() {
      override def configure() {
        from("netty-http:http://localhost:" + port + "/api?matchOnUriPrefix=true").process(new Processor() {
          override def process(exchange: Exchange) {
            val request = exchange.getIn(classOf[NettyHttpMessage]).getHttpRequest
            val body = exchange.getIn.getBody(classOf[String])
            val x = gatewayRequestMapper.mapRequest(request)
            try {
              dispatch(x)
            } catch {
              case e: IllegalAccessException =>
                exchange.getIn.setHeader("ACL_EXCEPTION", true)

            }
            if (body.isEmpty) {
              exchange.getIn.setBody(x.parameters)
            } else {
              val bean = applicationContext.getBean(x.service).getClass
              var method: Method = null
              val mc = new MethodCallback {
                override def doWith(m: Method): Unit = {
                  if (m.getName == x.operation) {
                    method = m
                  }

                }
              }
              ReflectionUtils.doWithMethods(bean, mc, null)
              val parameterType = method.getParameterTypes()(0)
              exchange.getIn.setBody(x.parameters :+ inputJsonMapper.readValue(body, parameterType))
            }
            exchange.getIn.setHeader("bean", x.service)
            exchange.getIn.setHeader("method", x.operation)
          }
        }).
          choice().
          when(header("ACL_EXCEPTION").isEqualTo(true)).setBody().constant("ACCESS DENIED").endChoice().
          otherwise().recipientList().simple("bean:${headers.bean}?method=${headers.method}&multiParameterArray=true").
          marshal().json(JsonLibrary.Jackson).log("${body}").endChoice()
      }
    })
  }

  override def doDispatch(gatewayRequest: GatewayRequest) {}

}