package micromix.services.restgateway.spring

import scala.collection.JavaConversions._
import java.io.Serializable
import java.util
import micromix.services.restgateway.api.Param

class InvoicesService {

  def load(id: Long) =
    InvoicesService.referenceInvoice

  def query(query: InvoiceQuery): java.util.List[Invoice] =
    new util.LinkedList(List(InvoicesService.referenceInvoice))

  def methodTakingAbstract(@Param(paramClass = classOf[SerializableId]) id: Serializable) =
    "response"

}

object InvoicesService {

  val referenceInvoice =
    new Invoice(1, "xxx")

}

class SerializableId extends Serializable
