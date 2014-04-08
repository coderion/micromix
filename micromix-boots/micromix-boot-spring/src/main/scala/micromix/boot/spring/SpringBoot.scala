package micromix.boot.spring

import org.springframework.context.ApplicationContext

case class SpringBoot() extends SpringBootSupportEnabled {

  // Members

  private var parentApplicationContext: ApplicationContext = null

  // Constructors

  def this(parentApplicationContext: ApplicationContext) = {
    this()
    this.parentApplicationContext = parentApplicationContext
  }

  // Overridden

  override def parentContext =
    parentApplicationContext

}