package micromix.services.restgateway.spring

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FunSuite}
import java.util.{List => JList}
import java.util.{Map => JMap}
import micromix.boot.spring.SpringBootSupportEnabled
import scala.collection.JavaConversions._
import scala.util.Random
import java.net.URL
import org.apache.commons.io.IOUtils
import test.micromix.services.restgateway.spring.WhitelistAclInterceptorConfiguration

@RunWith(classOf[JUnitRunner])
class RestGatewayConfigurationWhitelistAclInterceptorTest extends FunSuite with Matchers with SpringBootSupportEnabled {

  override def basePackages =
    Array("micromix.conflet.camel", "micromix.services.restgateway")


  override def namedBeansDefinitions =
    Map("invoices" -> classOf[InvoicesService])

  override def properties =
    Map("micromix.services.restgateway.spring.netty.port" -> Random.nextInt(60000))

  override def configurationClasses: JList[Class[_]] =
    List(classOf[WhitelistAclInterceptorConfiguration])

  test("Should override default request mapper.") {
    assertResult("ACCESS DENIED") {
      val httpPort = cachedProperties("micromix.services.restgateway.spring.netty.port")
      val is = new URL("http://localhost:" + httpPort + "/api/invoices/load/1").openStream
      IOUtils.toString(is)
    }
  }

}

