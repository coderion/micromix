package micromix.services.restgateway.api

import java.util
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConversions._
import org.springframework.aop.support.AopUtils
import java.lang.reflect.Modifier
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping

class DefaultApiExposer extends ApiExposer {

  @Autowired
  var applicationContext: ApplicationContext = _

  private val inputJsonMapper = new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_FINAL)

  override def apiSchema(services: util.List[String]): String = {
    val contract = new StringBuilder
    services.foreach {
      service =>
        contract append "Service URL: /" + service + "\n"
        val bean = applicationContext.getBean(service)
        val serviceClass = AopUtils.getTargetClass(bean)
        serviceClass.getMethods.foreach {
          m =>
            if (!List("notifyAll", "notify", "getClass", "hashCode", "toString", "wait", "equals").contains(m.getName)) {
              contract append "\nOperation: /" + m.getName
              if (!m.getParameterTypes.isEmpty) {
                val isAbstract = Modifier.isAbstract(m.getParameterTypes()(0).getModifiers)
                if (isAbstract && !m.getParameterAnnotations()(0).isEmpty && m.getParameterAnnotations()(0)(0).annotationType() == classOf[Param]) {
                  val ins = m.getParameterAnnotations()(0)(0).asInstanceOf[Param].paramClass().newInstance()
                  contract append "\nArgument to POST: " + inputJsonMapper.writeValueAsString(ins)
                } else if (!isAbstract && m.getParameterTypes()(0) != classOf[Object]) {
                  val ins = m.getParameterTypes()(0).newInstance()
                  contract append "\nArgument to POST: " + inputJsonMapper.writeValueAsString(ins)
                } else if (isAbstract) {
                  contract append "/{" + m.getParameterTypes()(0) + "}"
                }
              }
              contract append "\n"
            }
        }
    }
    println()
    println(contract.toString())
    contract.toString()
  }

}
