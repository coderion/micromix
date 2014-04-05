package micromix.services.restgateway.api

import org.scalatest.FunSuite
import micromix.boot.spring.SpringBootSupportEnabled

import scala.collection.JavaConversions._
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import micromix.services.restgateway.spring.DefaultGatewayRequest
import FixedTokenAuthGatewayInterceptor._

@RunWith(classOf[JUnitRunner])
class FixedTokenAuthGatewayInterceptorTest extends FunSuite with SpringBootSupportEnabled {

  override def singletons =
    List(FixedTokenAuthGatewayInterceptor())

  test("Should inject default token.") {
    val interceptor = context.getBean(classOf[FixedTokenAuthGatewayInterceptor])
    assertResult(defaultToken) {
      interceptor.expectedToken
    }
  }

  test("Should match token.") {
    val interceptor = context.getBean(classOf[FixedTokenAuthGatewayInterceptor])
    assertResult(true) {
      interceptor.intercept(DefaultGatewayRequest(Map(tokenHeader -> defaultToken), null, null, null))
    }
  }

  test("Should not match token.") {
    val interceptor = context.getBean(classOf[FixedTokenAuthGatewayInterceptor])
    assertResult(false) {
      interceptor.intercept(DefaultGatewayRequest(Map(tokenHeader -> "someRandomToken"), null, null, null))
    }
  }

  test("Should not match if no token.") {
    val interceptor = context.getBean(classOf[FixedTokenAuthGatewayInterceptor])
    assertResult(false) {
      interceptor.intercept(DefaultGatewayRequest(Map[String, String](), null, null, null))
    }
  }

}
