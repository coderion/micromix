package micromix.boot.spring

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition
import java.util.{List => JList, Map => JMap, Collections}
import Collections._

import scala.collection.JavaConversions._

trait SpringBootSupport {

  def basePackage =
    System.getProperty("micromix.boot.spring.basepackage", "micromix")

  def configurationClasses: JList[Class[_]] =
    emptyList()

  def beans: JMap[String, Class[_]] =
    emptyMap()

  val context = new AnnotationConfigApplicationContext(basePackage)
  configurationClasses.foreach(context.register(_))
  beans.foreach {
    bean =>
      context.registerBeanDefinition(bean._1, new AnnotatedGenericBeanDefinition(bean._2))
  }

}