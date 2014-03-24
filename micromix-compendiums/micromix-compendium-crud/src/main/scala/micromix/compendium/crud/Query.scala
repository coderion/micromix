package micromix.compendium.crud

trait Query {

  def firstPage: Long

  def count: Long

  def sortBy: String

  def isAscending: Boolean

}
