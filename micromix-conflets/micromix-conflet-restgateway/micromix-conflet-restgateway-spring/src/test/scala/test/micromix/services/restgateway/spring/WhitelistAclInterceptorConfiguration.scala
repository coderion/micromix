package test.micromix.services.restgateway.spring

import org.springframework.context.annotation.{Bean, Configuration}
import micromix.services.restgateway.api.WhitelistAclGatewayInterceptor
import scala.collection.JavaConversions._

@Configuration
class WhitelistAclInterceptorConfiguration {

  @Bean
  def whitelistAclInterceptor =
    new WhitelistAclGatewayInterceptor(Set("someBeanName"))

}
