package micromix.services.restgateway.spring

import java.util.UUID

import io.fabric8.process.spring.boot.actuator.camel.rest.Headers
import micromix.conflet.restgateway.FixedTokenAuthGatewayInterceptor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.netty.NettyConstants
import org.apache.camel.component.netty.http.NettyHttpMessage
import org.apache.camel.{LoggingLevel, _}
import org.apache.camel.model.dataformat.JsonLibrary
import org.jboss.netty.buffer.CompositeChannelBuffer
import org.jboss.netty.channel.ChannelHandlerContext
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.annotation.Configuration

import scala.collection.JavaConversions._

class CustomNettyGatewayEndpointRoute(val nettyServerName: String, val contextPath: String) extends RoutesBuilder {

  @Autowired
  var restPipelineProcessor: RestPipelineProcessor = _

  @Value("${micromix.api.mode:PRODUCTION}")
  var apiMode: String = _

  @Value("${micromix.api.logging:false}")
  var apiLogging: Boolean = _

  val ID: String = "ID"

  def addRoutesToCamelContext(cc: CamelContext) {

    cc.setUseBreadcrumb(false)
    cc.addRoutes(new RouteBuilder() {

      def loggingCondition: Predicate = new Predicate {
        override def matches(exchange: Exchange): Boolean = apiLogging
      }

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
            if (exchange.getProperties.containsKey(ID)) {
              log.error("ERROR ID {} {} ", exchange.getProperty(ID), ex.getMessage)
            } else {
              log.error("opis bledu: " + ex.getMessage)
            }
          }
        }).marshal().json(JsonLibrary.Jackson)

        from("netty-http:http://0.0.0.0/" + contextPath + "?matchOnUriPrefix=true&nettySharedHttpServer=#" + nettyServerName).
          process(new Processor() {
            override def process(exchange: Exchange) {
              exchange.setProperty(ID , UUID.randomUUID().toString)
            }
          }).
          wireTap("direct:requestLog-" + contextPath).
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
          wireTap("direct:responseLog-" + contextPath)

        from("direct:requestLog-" + contextPath).filter(loggingCondition).process(new Processor() {
          override def process(exchange: Exchange) {
            val request = exchange.getIn(classOf[NettyHttpMessage]).getHttpRequest
            val channelContext = exchange.getIn.getHeader(NettyConstants.NETTY_CHANNEL_HANDLER_CONTEXT, classOf[ChannelHandlerContext])

            log.info("ID {} Client IP {} ", exchange.getProperty(ID), channelContext.getChannel.getRemoteAddress)

            if (request.getContent != null && !request.getContent.isInstanceOf[CompositeChannelBuffer]) {
              log.info("ID {} {} ", exchange.getProperty(ID), request.toString.replace("\n", " "))
              if (request.getContent != null && !request.getContent.isInstanceOf[CompositeChannelBuffer]) {
                log.info("ID {} {} ", exchange.getProperty(ID), new String(request.getContent.array()).replace("\n", " "))
              }
            } else {
              log.info("ID {} {} ", exchange.getProperty(ID), request.getUri)
            }
          }
        })

        from("direct:responseLog-" + contextPath).filter(loggingCondition).process(new Processor() {
          override def process(exchange: Exchange) {
            val response = exchange.getIn.getBody(classOf[String])
            log.info("ID {} {} ", exchange.getProperty(ID), response)
          }
        })
      }
    })
  }

}
