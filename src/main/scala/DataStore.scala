/**
 * Created by roman on 26.05.15.
 */
import scala.collection.mutable.{Set, Map}

trait DataStore {
  def addFile(file: String, hash: String): Unit
  def countDupes(file: String): Int
  def getAllDupesets: Map[String, Set[String]]
}
