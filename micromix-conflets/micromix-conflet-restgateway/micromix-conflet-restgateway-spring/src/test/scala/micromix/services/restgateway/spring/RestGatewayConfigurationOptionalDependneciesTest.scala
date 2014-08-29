package micromix.services.restgateway.spring

import java.util.{List => JList, Map => JMap}

import micromix.boot.spring.SpringBootSupportEnabled
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

import scala.collection.JavaConversions._
import scala.util.Random

@RunWith(classOf[JUnitRunner])
class RestGatewayConfigurationOptionalDependneciesTest extends FunSuite with Matchers with SpringBootSupportEnabled {

  override def properties =
    Map("micromix.services.restgateway.spring.netty.port" -> Random.nextInt(60000))

  override def namedBeansDefinitions =
    Map("requestMapper" -> classOf[MockRequestMapper])

  test("Should override default port.") {
    val xport = context.getBean(classOf[RestGatewayConfiguration]).port
    val port = cachedProperties("micromix.services.restgateway.spring.netty.port")
    xport should be(port)
  }

}