package micromix.conflet.camel.spring

import org.scalatest.{Matchers, FunSuite}
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.apache.camel.CamelContext
import org.apache.camel.spring.SpringCamelContext
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CamelConfigurationTest extends FunSuite with Matchers {

  val applicationContext = new AnnotationConfigApplicationContext(classOf[CamelConfiguration])

  test("Should create Spring-aware CamelContext") {
    val camelContext = applicationContext.getBean(classOf[CamelContext])
    camelContext.getClass shouldEqual classOf[SpringCamelContext]
  }

}
