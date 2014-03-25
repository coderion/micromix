package micromix.compendium.crud

import scala.beans.BeanProperty

class BaseQuery(
                 @BeanProperty var firstPage: Long,
                 @BeanProperty var count: Long,
                 @BeanProperty var sortBy: String,
                 @BeanProperty var isAscending: Boolean
                 ) extends Query {

  def this() = this(0, -1, "id", true)

}