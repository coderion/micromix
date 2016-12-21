package micromix.services.restgateway.spring

import com.fasterxml.jackson.databind.{JsonMappingException, ObjectMapper}
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import io.fabric8.process.spring.boot.actuator.camel.rest._
import io.netty.buffer.CompositeByteBuf
import io.netty.channel.ChannelHandlerContext
import org.apache.camel.component.netty4.NettyConstants
import org.apache.camel.component.netty4.http.NettyHttpMessage
import org.apache.camel.{Exchange, Processor}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

import scala.collection.JavaConversions._

class RestPipelineProcessor(restInterceptor: RestInterceptor) extends RestPipeline[Exchange](restInterceptor) with Processor {

  @Autowired(required = false)
  var gatewayRequestMapper: RestRequestMapper[NettyRequest] = new NettyRestRequestMapper

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

  private def headerCleaner(exchange: Exchange) = {


    exchange.getIn.getHeaders.foreach(kv => {
      var key = kv._1
      if (key.contains("\t") || key.contains("\r") || key.contains("\n")) {
        exchange.getIn.getHeaders.remove(key)
      }
      if (kv._2 != null) {
        if (kv._2.toString.contains("\t") || kv._2.toString.contains("\r") || kv._2.toString.contains("\n")) {
          exchange.getIn.getHeaders.remove(key)
        }
      }
    })

    if (exchange.getIn.getHeaders.get("Location") != null ) {
      exchange.getIn.getHeaders.remove("Location")
    }

//    if (exchange.getIn.getHeaders.get("Content-Type") != null ) {
//      exchange.getIn.getHeaders.remove("Content-Type")
//    }

    if (exchange.getIn.getHeaders.get("Set-Cookie") != null ) {
      exchange.getIn.getHeaders.remove("Set-Cookie")
    }

    if (exchange.getIn.getHeaders.get("Cookie") != null ) {
      exchange.getIn.getHeaders.remove("Cookie")
    }

  }

  override def process(exchange: Exchange) {
    Headers.clear()
    headerCleaner(exchange)
    val request = exchange.getIn(classOf[NettyHttpMessage]).getHttpRequest
    val channelContext = exchange.getIn.getHeader(NettyConstants.NETTY_CHANNEL_HANDLER_CONTEXT, classOf[ChannelHandlerContext])
    var body: String = null
    if (request.content().isInstanceOf[CompositeByteBuf]) {
      body = exchange.getIn.getBody(classOf[String])
    } else {
      body = new String(request.content().array())
    }
    val x = gatewayRequestMapper.mapRequest(NettyRequest(request, channelContext))
    val bean = applicationContext.getBean(x.service).getClass
    val method = bean.getDeclaredMethods.find(_.getName == x.operation) match {
      case Some(m) => m
      case None => throw new IllegalArgumentException("No such method")
    }
    if (method.getReturnType == classOf[Array[Byte]]) {
      exchange.getIn.setHeader("BINARY", true)
    }
    try {
      if (!exchange.getIn.getHeader(Exchange.HTTP_METHOD).toString.equalsIgnoreCase("OPTIONS")) {
        dispatch(x, exchange)
      }
    } catch {
      case e: IllegalAccessException =>
        exchange.getIn.setHeader("ACL_EXCEPTION", true)
    }
    if (body.isEmpty) {
      exchange.getIn.setBody(x.parameters.zipWithIndex.map(p => exchange.getContext.getTypeConverter.convertTo(method.getParameterTypes()(p._2), p._1)))
    } else {
      val parameterType = method.getParameterTypes()(method.getParameterTypes().length-1)

      try {
        exchange.getIn.setBody(x.parameters.zipWithIndex.map(p => exchange.getContext.getTypeConverter.convertTo(method.getParameterTypes()(p._2), p._1)) :+ resolveJsonMapper(exchange).readValue(body, parameterType))
      } catch {

        case e: JsonMappingException =>
          if (e.getMessage.contains("Invalid numeric value: Leading zeroes not allowed")) {
            throw new RestCodeException("3001", "Invalid numeric value")
          }
          if (e.getMessage.contains("Unrecognized token") || e.getMessage.contains("Unexpected character")) {
            val split = e.getMessage.split("'")
            throw new RestCodeException("3001", "Value not found: " + split(1))
          }
          if (e.getMessage.contains("Can not construct instance of")) {
            val split = e.getMessage.split("\\s+")
            val model = split(5)
            val enum = model.split('.').last
            if (e.getMessage.contains("from String value")) {
              val model = split(9)
              throw new RestCodeException("3001", "Value not found: " + model + " " + enum)
            } else {
              throw new RestCodeException("3001", "Value not found: " + enum)
            }
          }
          if (e.getMessage.contains("Can not deserialize instance of java.util.Date")) {
            throw new RestCodeException("3001", "Value not found: Date")
          }
          throw new RestCodeException("3001")

        case e: InvalidFormatException =>
          if (e.getMessage.contains("Can not construct instance of")) {
            val split = e.getMessage.split("\\s+")
            val model = split(5)
            val enum = model.split('.').last
            if (e.getMessage.contains("from String value")) {
              val model = split(9)
              throw new RestCodeException("3002", "Value not found: " + model + " " + enum)
            } else {
              throw new RestCodeException("3002", "Value not found: " + enum)
            }
          }
          throw new RestCodeException("3002")
        case e: Exception =>
          throw new RestCodeException("3003")
      }
    }
    exchange.getIn.setHeader("bean", x.service)
    exchange.getIn.setHeader("method", x.operation)
  }

  protected override def doDispatch(restRequest: RestRequest, rsp: Exchange): Exchange =
    rsp

}
