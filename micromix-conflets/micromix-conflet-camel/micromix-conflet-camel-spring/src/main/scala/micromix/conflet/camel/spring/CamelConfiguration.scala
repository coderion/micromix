package micromix.conflet.camel.spring

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.context.ApplicationContext
import org.apache.camel.spring.SpringCamelContext
import org.springframework.beans.factory.annotation.Autowired
import org.apache.camel.{CamelContext, RoutesBuilder}

import scala.collection.JavaConversions._
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import micromix.boot.spring.BootCallback

@Configuration
class CamelConfiguration {

  @Autowired
  var applicationContext: ApplicationContext = _

  @Bean(initMethod = "start", destroyMethod = "stop")
  def camelContext = {
    val context = new SpringCamelContext(applicationContext)

    context
  }

  @Bean
  def xxx = new BootCallback {
    override def afterBoot() {
      val context = applicationContext.getBean(classOf[CamelContext])
      var camelRoutes = applicationContext.getBeansOfType(classOf[RoutesBuilder]).values()
      if (camelRoutes != null) {
        camelRoutes.foreach(route => context.addRoutes(route))
      }
    }
  }

  //  @Bean
  //  def xxx = new BeanPostProcessor {
  //
  //    override def postProcessBeforeInitialization(bean: scala.Any, beanName: String): AnyRef = bean.asInstanceOf[AnyRef]
  //
  //    override def postProcessAfterInitialization(bean: scala.Any, beanName: String): AnyRef = {
  //      if(bean.isInstanceOf[CamelContext]) {
  //        val context = bean.asInstanceOf[CamelContext]
  //        var camelRoutes = applicationContext.getBeansOfType(classOf[RoutesBuilder]).values()
  //        if (camelRoutes != null) {
  //          camelRoutes.foreach(route => context.addRoutes(route))
  //        }
  //      }
  //        bean.asInstanceOf[AnyRef]
  //    }
  //  }

}
