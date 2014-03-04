package micromix.boot.spring

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition
import java.util.{List => JList, Map => JMap, Collections}
import Collections._

import scala.collection.JavaConversions._

trait SpringBootSupport {

  protected val cachedProperties = properties

  def basePackage =
    System.getProperty("micromix.boot.spring.basepackage", "micromix")

  def configurationClasses: JList[Class[_]] =
    emptyList()

  def beans: JMap[String, Class[_]] =
    emptyMap()

  def properties: JMap[String, Any] =
    emptyMap()

  cachedProperties.foreach(property => System.setProperty(property._1, property._2.toString))
  val context = new AnnotationConfigApplicationContext
  context.register(classOf[BootstrapConfiguration])
  beans.foreach {
    bean =>
      context.registerBeanDefinition(bean._1, new AnnotatedGenericBeanDefinition(bean._2))
  }
  configurationClasses.foreach(context.register(_))
  context.scan(basePackage)
  context.refresh()
  context.getBeansOfType(classOf[BootCallback]).values().foreach(_.afterBoot())

}