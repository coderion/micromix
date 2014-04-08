package micromix.boot.spring

import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.InitializingBean

class DelegatingSpringBoot extends SpringBootSupport with InitializingBean {

  // Members

  @Autowired
  private var currentApplicationContext: ApplicationContext = _

  // Bean life cycle

  override def afterPropertiesSet() {
    initialize()
  }

  // Overridden

  override def parentContext =
    currentApplicationContext

}