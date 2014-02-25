package micromix.conflet.camel.spring

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.context.ApplicationContext
import org.apache.camel.spring.SpringCamelContext
import org.springframework.beans.factory.annotation.Autowired
import org.apache.camel.RoutesBuilder

import scala.collection.JavaConversions._

@Configuration
class CamelConfiguration {

  @Autowired
  var applicationContext: ApplicationContext = _

  @Autowired(required = false)
  var camelRoutes: java.util.List[RoutesBuilder] = _

  @Bean(initMethod = "start", destroyMethod = "stop")
  def camelContext = {
    val context = new SpringCamelContext(applicationContext)
    if (camelRoutes != null) {
      camelRoutes.foreach(route => context.addRoutes(route))
    }
    context
  }

}
