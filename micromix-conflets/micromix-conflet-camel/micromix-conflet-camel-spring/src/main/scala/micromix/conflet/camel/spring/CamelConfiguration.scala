package micromix.conflet.camel.spring

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.context.ApplicationContext
import org.apache.camel.spring.SpringCamelContext
import org.springframework.beans.factory.annotation.Autowired

@Configuration
class CamelConfiguration {

  @Autowired
  var applicationContext: ApplicationContext = _

  @Bean(initMethod = "start", destroyMethod = "stop")
  def camelContext =
    new SpringCamelContext(applicationContext)

}
