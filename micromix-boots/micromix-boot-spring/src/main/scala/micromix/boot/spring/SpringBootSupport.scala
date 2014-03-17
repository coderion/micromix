package micromix.boot.spring

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition
import java.util.{List => JList, Map => JMap, Collections}
import Collections._

import scala.collection.JavaConversions._

trait SpringBootSupport {

  // Members

  protected val context = new AnnotationConfigApplicationContext

  protected val cachedProperties = properties

  // Configuration callbacks

  protected def properties: JMap[String, Any] =
    emptyMap()

  def basePackages =
    Array(System.getProperty("micromix.boot.spring.basepackage", "micromix"))

  def configurationClasses: JList[Class[_]] =
    emptyList()

  def beans: JMap[String, Class[_]] =
    emptyMap()

  def initialize() {
    cachedProperties.foreach(property => System.setProperty(property._1, property._2.toString))
    context.register(classOf[BootstrapConfiguration])
    beans.foreach {
      bean =>
        context.registerBeanDefinition(bean._1, new AnnotatedGenericBeanDefinition(bean._2))
    }
    configurationClasses.foreach(context.register(_))
    context.scan(basePackages: _*)
    context.refresh()
    context.getBeansOfType(classOf[BootCallback]).values().foreach(_.afterBoot())
  }

}