package micromix.compendium.crud

import org.springframework.data.domain.PageRequest
import java.io.Serializable
import org.springframework.data.repository.PagingAndSortingRepository
import org.apache.commons.lang3.reflect.FieldUtils
import scala.collection.JavaConversions._

//@Transactional
abstract class SpringDataRepositoryCrudService[T, E, ID <: Serializable, D <: PagingAndSortingRepository[E, ID], Q <: Query]
  extends CrudService[T, ID, Q] {

  protected def dao: D

  override def save[S <: T](dto: S): S = {
    var entity: E = convertDtoToEntity(dto)
    entity = dao.save(entity)
    if (isNew(entity)) {
      assignId(dto, resolveId(entity))
    }
    convertEntityToDto(entity).asInstanceOf[S]
  }

  protected def isNew(entity: E): Boolean =
    FieldUtils.readDeclaredField(entity, "id", true) match {
      case null => false
      case anyRef: AnyRef => {
        if (anyRef.isInstanceOf[Long]) {
          val longId = anyRef.asInstanceOf[Long]
          return longId < 0
        } else {
          return false
        }
      }
    }

  protected def resolveId(entity: E): Serializable =
    FieldUtils.readDeclaredField(entity, "id", true).asInstanceOf[Serializable]

  protected def assignId(dto: T, id: Serializable) {
    FieldUtils.writeDeclaredField(dto, "id", id, true)
  }

  override def save[S <: T](iterable: java.lang.Iterable[S]): java.lang.Iterable[S] =
    iterable.map(x => save(x)).toList

  override def findOne(id: ID): T =
    convertEntityToDto(dao.findOne(id))

  def findByQuery(query: Q): java.util.List[T] = {
    //    val page: Int = query.firstPage.asInstanceOf[Int]
    //    val pageSize: Int = query.count.asInstanceOf[Int]
    //    val direction: Sort.Direction = if (query.isAscending) ASC else DESC
    //    val sortBy: String = query.sortBy
    //    if (pageSize < 1) {
    //      dao.fin
    //      return ImmutableList.copyOf(transform(dao.findAll(queryToSpecification(query), new Sort(direction, sortBy)), entityToDtoFunction))
    //    }
    //    else {
    //      val entities: List[E] = newLinkedList(dao.findAll(queryToSpecification(query), new PageRequest(page, pageSize, direction, sortBy)))
    //      return ImmutableList.copyOf(transform(entities, entityToDtoFunction))
    //    }
    null
  }

  def countByQuery(query: Q): Long = 0

  //  def delete(id: Long) {
  //    dao.delete(id)
  //  }
  //
  //  def deleteMany(ids: Long*) {
  //    for (id <- ids) {
  //      dao.delete(id)
  //    }
  //  }

  protected def convertEntityToDto(entity: E): T

  protected def convertDtoToEntity(dto: T): E

}