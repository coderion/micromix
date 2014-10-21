package micromix.services.restgateway.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping
import io.fabric8.process.spring.boot.actuator.camel.rest._
import micromix.conflet.restgateway.FixedTokenAuthGatewayInterceptor
import org.apache.camel.component.netty.http.NettyHttpMessage
import org.apache.camel.{Exchange, Processor}
import org.jboss.netty.handler.codec.http.HttpRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

class RestPipelineProcessor(restInterceptor: RestInterceptor) extends RestPipeline[Exchange](restInterceptor) with Processor {

  @Autowired(required = false)
  var gatewayRequestMapper: RestRequestMapper[HttpRequest] = new NettyRestRequestMapper

  @Autowired
  private var applicationContext: ApplicationContext = _

  private val inputJsonMapper = new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_FINAL)

  private val plainInputJsonMapper = new ObjectMapper

  private def resolveJsonMapper(exchange: Exchange) = {
    val api = exchange.getIn.getHeader("PLAIN_API", classOf[String])
    if (java.lang.Boolean.parseBoolean(api)) {
      plainInputJsonMapper
    } else {
      inputJsonMapper
    }
  }

  override def process(exchange: Exchange) {
    Headers.clear()
    val request = exchange.getIn(classOf[NettyHttpMessage]).getHttpRequest
    val body = exchange.getIn.getBody(classOf[String])
    val x = gatewayRequestMapper.mapRequest(request)
    val bean = applicationContext.getBean(x.service).getClass
    val method = bean.getDeclaredMethods.find(_.getName == x.operation) match {
      case Some(m) => m
      case None => throw new IllegalArgumentException("No such method")
    }
    if (method.getReturnType == classOf[Array[Byte]]) {
      exchange.getIn.setHeader("BINARY", true)
    }
    try {
      dispatch(x, exchange)
    } catch {
      case e: IllegalAccessException =>
        exchange.getIn.setHeader("ACL_EXCEPTION", true)
    }
    if (body.isEmpty) {
      exchange.getIn.setBody(x.parameters.zipWithIndex.map(p => exchange.getContext.getTypeConverter.convertTo(method.getParameterTypes()(p._2), p._1)))
    } else {
      val parameterType = method.getParameterTypes()(method.getParameterTypes().length-1)
      exchange.getIn.setBody(x.parameters.zipWithIndex.map(p => exchange.getContext.getTypeConverter.convertTo(method.getParameterTypes()(p._2), p._1)) :+ resolveJsonMapper(exchange).readValue(body, parameterType))
    }
    exchange.getIn.setHeader("bean", x.service)
    exchange.getIn.setHeader("method", x.operation)
    exchange.getIn.setHeader("Access-Control-Allow-Origin", "*")
    exchange.getIn.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, " + FixedTokenAuthGatewayInterceptor.tokenHeader)
  }

  protected override def doDispatch(restRequest: RestRequest, rsp: Exchange): Exchange =
    rsp

}
