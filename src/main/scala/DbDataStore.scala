/**
 * Created by roman on 27.05.15.
 */

import slick.lifted.ProvenShape

import scala.collection.mutable.{Set, Map}
import scala.concurrent.Await
import scala.concurrent.duration._
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

class FileHashes(tag: Tag) extends Table[(Option[Int], String, String)](tag, "file_hashes") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def hash = column[String]("hash")

  def * = (id.?, name, hash)
}

class HashToCounts(tag: Tag) extends Table[(Option[Int], String, Int)](tag, "hash_to_counts") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def hash = column[String]("hash")
  def count = column[Int]("count")

  def * = (id.?, hash, count)
}

// TODO: think of more asynchronous way of dealing with things
// May need changes to DataStore interface

class DbDataStore extends DataStore {
  private val db = Database.forConfig("h2mem1")
  private val hashes = TableQuery[FileHashes]
  private val hashToCount = TableQuery[HashToCounts]

  {
    val setup = hashes.schema.create
    Await.ready(db.run(setup), Duration.Inf)
  }

  override def addFile(file: String, hash: String): Unit = {
    Await.ready(db.run(hashes += (None, file, hash)), Duration.Inf)

  }

  override def getAllDupesets: Map[String, Set[String]] = {
    Map[String, Set[String]]()
  }

  override def countDupes(file: String): Int = {
    0
  }

  override def removeFile(file: String): Unit = {

  }
}
