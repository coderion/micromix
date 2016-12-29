package micromix.services.restgateway.spring

import java.net.InetSocketAddress
import java.nio.charset.Charset

import io.fabric8.process.spring.boot.actuator.camel.rest.{Headers, RestCodeException, RestCodeResponse}
import io.netty.buffer.CompositeByteBuf
import io.netty.channel.ChannelHandlerContext
import micromix.conflet.restgateway.FixedTokenAuthGatewayInterceptor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.netty4.NettyConstants
import org.apache.camel.component.netty4.http.NettyHttpMessage
import org.apache.camel.model.dataformat.JsonLibrary
import org.apache.camel.{LoggingLevel, _}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}

import scala.collection.JavaConversions._

class CustomNettyGatewayEndpointRoute(val nettyServerName: String, val contextPath: String) extends RoutesBuilder {

  @Autowired
  var restPipelineProcessor: RestPipelineProcessor = _

  @Value("${micromix.api.mode:PRODUCTION}")
  var apiMode: String = _

  @Value("${micromix.api.logging:false}")
  var apiLogging: Boolean = _

  @Value("${micromix.conflet.restgateway.extended.login:Micromix!@#}")
  var extendedLogging: String = _

  object LoggingHeaders extends Enumeration {
    val REQUEST_METHOD: String = "REQUEST_METHOD"
    val REQUEST_URI: String = "REQUEST_URI"
    val REQUEST_HEADERS: String = "REQUEST_HEADERS"
    val REQUEST_BODY: String = "REQUEST_BODY"
    val RESPONSE_BODY: String = "RESPONSE_BODY"
    val REMOTE: String = "REMOTE"
  }

  def addRoutesToCamelContext(cc: CamelContext) {

    cc.setUseBreadcrumb(false)
    cc.addRoutes(new RouteBuilder() {

      val LOGGER = LoggerFactory.getLogger(contextPath)

      def loggingCondition: Predicate = new Predicate {
        override def matches(exchange: Exchange): Boolean = apiLogging
      }

      override def configure() {

        onException(classOf[java.lang.Exception]).handled(true).
          process(new SecurityPipelineProcessor()).
          process(new Processor() {
            override def process(exchange: Exchange) {
              val ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, classOf[java.lang.Exception])
              if (apiMode.equalsIgnoreCase("PRODUCTION")) {
                if (ex.getClass.getSimpleName.equalsIgnoreCase("DisabledException") ||
                  ex.getClass.getSimpleName.equalsIgnoreCase("BadCredentialsException") ||
                  ex.getClass.getSimpleName.equalsIgnoreCase("GeneralSecurityException") ||
                  ex.getClass.getSimpleName.equalsIgnoreCase("LoginMismatchedException")) {
                  exchange.getIn.setBody(ex.getClass.getSimpleName + ": " + ex.getMessage)
                } else {
                  if (ex.getClass.getSimpleName.equalsIgnoreCase("RestCodeException")) {
                    val exception: RestCodeException = ex.asInstanceOf[RestCodeException]
                    val response = new RestCodeResponse(exception.getCode, exception.getMessageInfo)
                    exchange.getIn.setBody(response)
                  } else {
                    if (exchange.getIn.getHeaders.containsKey(FixedTokenAuthGatewayInterceptor.extendedLoginHeader) &&
                      exchange.getIn.getHeader(FixedTokenAuthGatewayInterceptor.extendedLoginHeader, classOf[String]).equals(extendedLogging)) {
                      exchange.getIn.setBody(ex.getClass.getSimpleName + ": " + ex.getMessage)
                    } else {
                      exchange.getIn.setBody("ApiException: API Error")
                    }
                  }
                }
              } else {
                if (ex.getClass.getSimpleName.equalsIgnoreCase("RestCodeException")) {
                  val exception: RestCodeException = ex.asInstanceOf[RestCodeException]
                  val response = new RestCodeResponse(exception.getCode, exception.getMessageInfo)
                  exchange.getIn.setBody(response)
                } else {
                  exchange.getIn.setBody(ex.getClass.getSimpleName + ": " + ex.getMessage)
                }
              }
              exchange.getIn.setHeader(Exchange.HTTP_RESPONSE_CODE, 400)
            }
          }).wireTap("direct:error-log-" + contextPath).marshal().json(JsonLibrary.Jackson)


        from("netty4-http:http://0.0.0.0/" + contextPath + "?matchOnUriPrefix=true&httpMethodRestrict=OPTIONS,GET,POST,PUT,DELETE&nettySharedHttpServer=#" + nettyServerName).
          choice().
          when(header(Exchange.HTTP_METHOD).isEqualTo("OPTIONS")).//setBody().constant("").endChoice().
            process(new SecurityPipelineProcessor()).endChoice().
          otherwise().
            process(new Processor() {
              override def process(exchange: Exchange) {
                val request = exchange.getIn(classOf[NettyHttpMessage]).getHttpRequest
                val channelContext = exchange.getIn.getHeader(NettyConstants.NETTY_CHANNEL_HANDLER_CONTEXT, classOf[ChannelHandlerContext])
                val socketAddress = channelContext.channel().remoteAddress().asInstanceOf[InetSocketAddress]

                exchange.getIn().setHeader(LoggingHeaders.REQUEST_METHOD, request.getMethod)
                exchange.getIn().setHeader(LoggingHeaders.REQUEST_URI, request.getUri)
                exchange.getIn().setHeader(LoggingHeaders.REQUEST_HEADERS, request.headers().entries().map(entry => entry.getKey + "->" + entry.getValue).mkString(", "))
                exchange.getIn().setHeader(LoggingHeaders.REMOTE, socketAddress.getAddress.getHostAddress)
                exchange.getIn().setHeader(LoggingHeaders.REQUEST_BODY, "NONE OR HIDDEN")

                if (request.content() != null && !request.content().isInstanceOf[CompositeByteBuf] &&
                  request.content().array().length > 0 &&
                  request.getUri != null && !request.getUri.contains("authenticate")) {
                  exchange.getIn().setHeader(LoggingHeaders.REQUEST_BODY, request.content().toString(Charset.defaultCharset()))
                }
              }
            }).
            process(new SecurityPipelineProcessor()).
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
          }).endChoice().
            process(new Processor() {
              override def process(exchange: Exchange) {
                val response = exchange.getIn.getBody(classOf[String])

                exchange.getIn().setHeader(LoggingHeaders.RESPONSE_BODY, response)
              }
            }).endChoice()
          .end().
          wireTap("direct:log-" + contextPath)

        from("direct:log-" + contextPath).filter(loggingCondition).process(new Processor() {
          override def process(exchange: Exchange) {

            val message = "%s %s %s \n%s \n%s \n%s".format(
              exchange.getIn().getHeader(LoggingHeaders.REMOTE),
              exchange.getIn().getHeader(LoggingHeaders.REQUEST_METHOD),
              exchange.getIn().getHeader(LoggingHeaders.REQUEST_URI),
              exchange.getIn().getHeader(LoggingHeaders.REQUEST_HEADERS),
              exchange.getIn().getHeader(LoggingHeaders.REQUEST_BODY),
              exchange.getIn().getHeader(LoggingHeaders.RESPONSE_BODY)
            )
            LOGGER.info(message)
          }
        })

        from("direct:error-log-" + contextPath).process(new Processor() {
          override def process(exchange: Exchange) {

            val ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, classOf[java.lang.Exception])

            val message = "%s %s %s \n%s \n%s \n%s".format(
              exchange.getIn().getHeader(LoggingHeaders.REMOTE),
              exchange.getIn().getHeader(LoggingHeaders.REQUEST_METHOD),
              exchange.getIn().getHeader(LoggingHeaders.REQUEST_URI),
              exchange.getIn().getHeader(LoggingHeaders.REQUEST_HEADERS),
              exchange.getIn().getHeader(LoggingHeaders.REQUEST_BODY),
              ex.getClass.getSimpleName + ": " + ex.getMessage
            )
            LOGGER.error(message)
          }
        })

      }
    })
  }

}
