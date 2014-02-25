package micromix.boot.spring

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition

trait SpringBootSupport {

  def basePackage =
    System.getProperty("micromix.boot.spring.basepackage", "micromix")

  def configurationClasses =
    Seq[Class[_]]()

  def beans =
    Map[String, Class[_]]()

  val context = new AnnotationConfigApplicationContext(basePackage)
  configurationClasses.foreach(context.register(_))
  beans.foreach {
    bean =>
      context.registerBeanDefinition(bean._1, new AnnotatedGenericBeanDefinition(bean._2))
  }

}

object SpringBootSupport extends App with SpringBootSupport