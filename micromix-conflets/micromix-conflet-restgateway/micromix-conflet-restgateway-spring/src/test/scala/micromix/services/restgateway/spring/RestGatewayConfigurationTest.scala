package micromix.services.restgateway.spring

import java.net.URL
import java.util.{List => JList, Map => JMap}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping
import micromix.boot.spring.SpringBootSupportEnabled
import org.apache.commons.io.IOUtils
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.collection.JavaConversions._
import scala.util.Random

@RunWith(classOf[JUnitRunner])
class RestGatewayConfigurationTest extends FunSuite with SpringBootSupportEnabled {

  override def properties =
    Map("micromix.services.restgateway.spring.netty.port" -> Random.nextInt(60000))

  override def namedBeansDefinitions =
    Map("invoices" -> classOf[InvoicesService])

  val jsonMapper = new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_FINAL)

  val readJsonMapper = new ObjectMapper()

  test("Should load invoice.") {
    assertResult(InvoicesService.referenceInvoice.getId) {
      val httpPort = cachedProperties("micromix.services.restgateway.spring.netty.port")
      val is = new URL("http://localhost:" + httpPort + "/api/invoices/load/1").openStream
      readJsonMapper.readValue(is, classOf[Invoice]).getId
    }
  }

  test("Should find invoices by query.") {
    assertResult(List(InvoicesService.referenceInvoice.getTitle)) {
      val con = new URL("http://localhost:" + cachedProperties("micromix.services.restgateway.spring.netty.port") + "/api/invoices/query").openConnection()
      con.setDoOutput(true)
      con.setRequestProperty("Content-Type", "application/json")
      val out = con.getOutputStream
      jsonMapper.writeValue(out, new InvoiceQuery(1, "xxx"))
      val is = con.getInputStream
      readJsonMapper.readValue(is, classOf[JList[JMap[String, Any]]]).map(_.get("title"))
    }
  }

  test("Should handle polymorphic arguments.") {
    assertResult("response") {
      val con = new URL("http://localhost:" + cachedProperties("micromix.services.restgateway.spring.netty.port") + "/api/invoices/methodTakingAbstract").openConnection()
      con.setDoOutput(true)
      con.setRequestProperty("Content-Type", "application/json")
      val out = con.getOutputStream
      jsonMapper.writeValue(out, new SerializableId())
      val is = con.getInputStream
      jsonMapper.readValue(is, classOf[String])
    }
  }

  test("Should return exception.") {
    assertResult("ApiException: API Error") {
      val httpPort = cachedProperties("micromix.services.restgateway.spring.netty.port")
      val is = new URL("http://localhost:" + httpPort + "/api/invoices/error").openStream
      readJsonMapper.readValue(is, classOf[String])
    }
  }

  test("Should return bytes.") {
    assertResult("bytes FTW!".getBytes) {
      val httpPort = cachedProperties("micromix.services.restgateway.spring.netty.port")
      val is = new URL("http://localhost:" + httpPort + "/api/invoices/download").openStream
      IOUtils.toByteArray(is)
    }
  }

}