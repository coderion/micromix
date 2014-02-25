package micromix.services.restgateway.spring

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.springframework.context.annotation.{Bean, Configuration}
import java.net.URL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.apache.camel.spring.SpringCamelContext
import com.fasterxml.jackson.databind.ObjectMapper
import micromix.boot.spring.SpringBootSupport
import scala.collection.JavaConversions._

@RunWith(classOf[JUnitRunner])
class RestGatewayConfigurationTest extends FunSuite with SpringBootSupport {

  override def basePackage: String = "micromix.services.restgateway.spring"

  val jsonMapper = new ObjectMapper

  test("Should load invoice.") {
    assertResult(InvoicesService.referenceInvoice.getId) {
      val is = new URL("http://localhost:18080/api/invoices/load/1").openStream
      jsonMapper.readValue(is, classOf[Invoice]).getId
    }
  }

  test("Should find invoices by query.") {
    assertResult(List(InvoicesService.referenceInvoice.getTitle)) {
      val con = new URL("http://localhost:18080/api/invoices/query").openConnection()
      con.setDoOutput(true)
      con.setRequestProperty("Content-Type", "application/json")
      val out = con.getOutputStream
      jsonMapper.writeValue(out, new InvoiceQuery(1, "xxx"))
      val is = con.getInputStream
      jsonMapper.readValue(is, classOf[java.util.List[java.util.Map[String, Any]]]).map(_.get("title"))
    }
  }

}

@Configuration
class TestConfig {

  @Autowired
  var applicationContext: ApplicationContext = _

  @Bean
  def invoices =
    new InvoicesService()

  @Bean(initMethod = "start")
  def camelContext =
    new SpringCamelContext(applicationContext)

}
