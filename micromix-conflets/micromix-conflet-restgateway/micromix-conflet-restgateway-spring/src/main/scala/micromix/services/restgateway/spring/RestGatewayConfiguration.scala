package micromix.services.restgateway.spring

import org.springframework.context.annotation.{Bean, Configuration}
import org.apache.camel.{Exchange, Processor, CamelContext}
import org.springframework.beans.factory.annotation.Autowired
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.netty.http.NettyHttpMessage
import org.apache.camel.model.dataformat.JsonLibrary
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationContext
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Method
import org.springframework.util.ReflectionUtils.MethodCallback

@Configuration
class RestGatewayConfiguration {

  @Autowired
  var camelContext: CamelContext = _

  val gatewayRequestMapper = new NettyGatewayRequestMapper

  val mapper = new ObjectMapper()

  @Autowired
  var applicationContext: ApplicationContext = _

  @Bean
  def nettyEndpointRoute =
    new RouteBuilder() {
      override def configure() {
        from("netty-http:http://localhost:18080/api?matchOnUriPrefix=true").process(new Processor() {
          override def process(exchange: Exchange) {
            val request = exchange.getIn(classOf[NettyHttpMessage]).getHttpRequest
            val body = exchange.getIn.getBody(classOf[String])
            val x = gatewayRequestMapper.mapRequest(request)
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
              exchange.getIn.setBody(x.parameters :+ mapper.readValue(body, parameterType))
            }
            exchange.getIn.setHeader("bean", x.service)
            exchange.getIn.setHeader("method", x.operation)
          }
        }).recipientList().simple("bean:${headers.bean}?method=${headers.method}&multiParameterArray=true").
          marshal().json(JsonLibrary.Jackson).log("${body}")
      }
    }

}