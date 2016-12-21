package micromix.services.restgateway.spring

import io.fabric8.process.spring.boot.actuator.camel.rest.{NopRestInterceptor, RestInterceptor}
import org.apache.camel.component.netty4.http.{DefaultNettySharedHttpServer, NettySharedHttpServerBootstrapConfiguration}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.annotation.{Bean, Configuration}

/**
  * Created by marek on 14.12.16.
  */
@Configuration
class RestGatewayConfiguration {

  @Value("${micromix.services.restgateway.spring.netty.port:18080}")
  var port: Int = _

  @Value("${micromix.api.mode:PRODUCTION}")
  var apiMode: String = _

  @Value("${micromix.api.logging:false}")
  var apiLogging: Boolean = _

  @Value("${micromix.services.restgateway.spring.netty.chunkedMaxContentLength:26214400}")
  var chunkedMaxContentLength: Int = _

  @Autowired(required = false)
  var gatewayInterceptor: RestInterceptor = new NopRestInterceptor

  @Bean
  def nettyGatewayEndpointRoute =
    new NettyGatewayEndpointRoute(pipelineProcessor, apiMode, apiLogging)

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