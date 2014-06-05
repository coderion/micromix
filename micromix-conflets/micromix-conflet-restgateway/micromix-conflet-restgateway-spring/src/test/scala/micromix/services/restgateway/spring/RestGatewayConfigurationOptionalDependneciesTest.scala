package micromix.services.restgateway.spring

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FunSuite}
import java.util.{List => JList}
import java.util.{Map => JMap}
import micromix.boot.spring.SpringBootSupportEnabled
import scala.collection.JavaConversions._
import scala.util.Random

@RunWith(classOf[JUnitRunner])
class RestGatewayConfigurationOptionalDependneciesTest extends FunSuite with Matchers with SpringBootSupportEnabled {

  override def properties =
    Map("micromix.services.restgateway.spring.netty.port" -> Random.nextInt(60000))

  override def namedBeansDefinitions =
    Map("requestMapper" -> classOf[MockRequestMapper])

  test("Should override default port.") {
    val xport = context.getBean(classOf[RestGatewayConfiguration]).nettyGatewayEndpointRoute.port
    val port = cachedProperties("micromix.services.restgateway.spring.netty.port")
    xport should be(port)
  }

}