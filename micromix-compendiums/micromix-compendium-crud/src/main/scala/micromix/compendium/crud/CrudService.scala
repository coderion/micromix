package micromix.compendium.crud

import org.springframework.data.repository.PagingAndSortingRepository
import java.util.{List => JList}

trait CrudService[T, ID <: java.io.Serializable, Q <: Query] extends PagingAndSortingRepository[T, ID] {

  def findByQuery(query: Q): JList[T]

  def countByQuery(query: Q): Long

}
