package micromix.services.restgateway.spring

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import java.util.{List => JList}
import java.util.{Map => JMap}
import com.fasterxml.jackson.databind.ObjectMapper
import micromix.boot.spring.SpringBootSupportEnabled
import scala.collection.JavaConversions._
import scala.util.Random
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping
import micromix.services.restgateway.api.{ApiExposer, DefaultApiExposer}

@RunWith(classOf[JUnitRunner])
class DefaultApiExposerTest extends FunSuite with SpringBootSupportEnabled {

  override def properties =
    Map("micromix.services.restgateway.spring.netty.port" -> Random.nextInt(60000))

  override def namedBeansDefinitions =
    Map("invoices" -> classOf[InvoicesService], "exposer" -> classOf[DefaultApiExposer])

  val jsonMapper = new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_FINAL)

  val readJsonMapper = new ObjectMapper()

  test("Should load invoice.") {
    val exposer = context.getBean(classOf[ApiExposer])
    exposer.apiSchema(List("invoices"))
  }

}