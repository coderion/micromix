package micromix.services.restgateway.spring

import scala.collection.JavaConversions._

class InvoicesService {

  def load(id: Long) =
    InvoicesService.referenceInvoice

  def query(query: InvoiceQuery): java.util.List[Invoice] =
    List(InvoicesService.referenceInvoice)

}

object InvoicesService {

  val referenceInvoice =
    new Invoice(1, "xxx")

}
