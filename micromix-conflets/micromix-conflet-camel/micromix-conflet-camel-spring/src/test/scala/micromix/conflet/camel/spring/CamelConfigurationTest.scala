package micromix.conflet.camel.spring

import org.scalatest.{Matchers, FunSuite}
import org.apache.camel.CamelContext
import org.apache.camel.spring.SpringCamelContext
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import micromix.boot.spring.SpringBootSupportEnabled

@RunWith(classOf[JUnitRunner])
class CamelConfigurationTest extends FunSuite with Matchers with SpringBootSupportEnabled {

  test("Should create Spring-aware CamelContext.") {
    val camelContext = context.getBean(classOf[CamelContext])
    camelContext.getClass shouldEqual classOf[SpringCamelContext]
  }

}
