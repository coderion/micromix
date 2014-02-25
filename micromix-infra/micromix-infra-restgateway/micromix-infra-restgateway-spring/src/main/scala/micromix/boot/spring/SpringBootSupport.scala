package micromix.boot.spring

import org.springframework.context.annotation.AnnotationConfigApplicationContext

trait SpringBootSupport {
  def basePackage = System.getProperty("micromix.boot.spring.basepackage", "micromix")

  val context = new AnnotationConfigApplicationContext(basePackage)
}

object SpringBootSupport extends App with SpringBootSupport