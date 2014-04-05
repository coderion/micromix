package micromix.services.restgateway.api

import org.scalatest.{BeforeAndAfter, FunSuite}
import micromix.boot.spring.SpringBootSupportEnabled

import scala.collection.JavaConversions._
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import micromix.services.restgateway.spring.DefaultGatewayRequest
import FixedTokenAuthGatewayInterceptor._
import org.springframework.beans.factory.annotation.Autowired

@RunWith(classOf[JUnitRunner])
class FixedTokenAuthGatewayInterceptorTest extends FunSuite with BeforeAndAfter with SpringBootSupportEnabled {

  @Autowired
  var interceptor: FixedTokenAuthGatewayInterceptor = _

  override def singletons =
    List(FixedTokenAuthGatewayInterceptor())

  before {
    autowire()
  }

  // Tests

  test("Should inject default token.") {
    assertResult(defaultToken) {
      interceptor.expectedToken
    }
  }

  test("Should match token.") {
    assertResult(true) {
      interceptor.intercept(DefaultGatewayRequest(Map(tokenHeader -> defaultToken), null, null, null))
    }
  }

  test("Should not match token.") {
    assertResult(false) {
      interceptor.intercept(DefaultGatewayRequest(Map(tokenHeader -> "someRandomToken"), null, null, null))
    }
  }

  test("Should not match if no token.") {
    assertResult(false) {
      interceptor.intercept(DefaultGatewayRequest(Map[String, String](), null, null, null))
    }
  }

}