package micromix.services.restgateway.spring

import java.util.UUID

import io.fabric8.process.spring.boot.actuator.camel.rest._
import io.netty.buffer.CompositeByteBuf
import io.netty.channel.ChannelHandlerContext
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.netty4.NettyConstants
import org.apache.camel.component.netty4.http.NettyHttpMessage
import org.apache.camel.model.dataformat.JsonLibrary
import org.apache.camel.{Headers => _, _}

import scala.collection.JavaConversions._



class NettyGatewayEndpointRoute(restPipelineProcessor: RestPipelineProcessor, apiMode: String, apiLogging: Boolean) extends RoutesBuilder {

  var ID: String = "ID"
  def addRoutesToCamelContext(cc: CamelContext) {

    cc.setUseBreadcrumb(false)
    cc.addRoutes(new RouteBuilder() {

      def loggingCondition: Predicate = new Predicate {
        override def matches(exchange: Exchange): Boolean = apiLogging
      }

      override def configure() {

        onException(classOf[java.lang.Exception]).handled(true).to("log:rest-error?level=ERROR").
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

        from("netty4-http:http://0.0.0.0/api?matchOnUriPrefix=true&nettySharedHttpServer=#sharedNettyHttpServer").
          process(new Processor() {
            override def process(exchange: Exchange) {
              exchange.setProperty(ID , UUID.randomUUID().toString)
            }
          }).
          wireTap("direct:requestLog").
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
          wireTap("direct:responseLog")

        from("direct:requestLog").filter(loggingCondition).process(new Processor() {
          override def process(exchange: Exchange) {
            val request = exchange.getIn(classOf[NettyHttpMessage]).getHttpRequest
            val channelContext = exchange.getIn.getHeader(NettyConstants.NETTY_CHANNEL_HANDLER_CONTEXT, classOf[ChannelHandlerContext])

            log.info("ID {} Client IP {} ", exchange.getProperty(ID), channelContext.channel().remoteAddress())

            if (request.content() != null && !request.content().isInstanceOf[CompositeByteBuf]) {
              log.info("ID {} {} ", exchange.getProperty(ID), request.toString.replace("\n", " "))
              if (request.content() != null && !request.content().isInstanceOf[CompositeByteBuf]) {
                log.info("ID {} {} ", exchange.getProperty(ID), new String(request.content().array()).replace("\n", " "))
              }
            } else {
              log.info("ID {} {} ", exchange.getProperty(ID), request.getUri)
            }
          }
        })

        from("direct:responseLog").filter(loggingCondition).process(new Processor() {
          override def process(exchange: Exchange) {
            val response = exchange.getIn.getBody(classOf[String])
            log.info("ID {} {} ", exchange.getProperty(ID), response)
          }
        })
      }
    })
  }

}