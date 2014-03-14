package micromix.services.restgateway.spring

import scala.collection.JavaConversions._
import java.io.Serializable
import java.util

class InvoicesService {

  def load(id: Long) =
    InvoicesService.referenceInvoice

  def query(query: InvoiceQuery): java.util.List[Invoice] =
    new util.LinkedList(List(InvoicesService.referenceInvoice))

  def methodTakingAbstract(id: Serializable) =
    "response"

}

object InvoicesService {

  val referenceInvoice =
    new Invoice(1, "xxx")

}

class SerializableId extends Serializable
