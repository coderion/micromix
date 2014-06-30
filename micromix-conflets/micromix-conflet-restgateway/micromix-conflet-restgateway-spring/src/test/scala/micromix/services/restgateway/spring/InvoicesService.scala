package micromix.services.restgateway.spring

import java.io.Serializable
import java.util

import micromix.services.restgateway.api.{Param, Returns}

import scala.collection.JavaConversions._

class InvoicesService {

  def load(id: Long): Invoice =
    InvoicesService.referenceInvoice

  @Returns(returnedClass = classOf[Invoice])
  def query(query: InvoiceQuery): java.util.List[Invoice] =
    new util.LinkedList(List(InvoicesService.referenceInvoice))

  def methodTakingAbstract(@Param(paramClass = classOf[SerializableId]) id: Serializable): String =
    "response"

  def error() {
    throw new RuntimeException("Error message!")
  }

  def download: Array[Byte] =
    "bytes FTW!".getBytes

}

object InvoicesService {

  val referenceInvoice =
    new Invoice(1, "xxx")

}

class SerializableId extends Serializable
