/**
 * Created by roman on 27.05.15.
 */

import slick.lifted.ProvenShape

import scala.collection.mutable.{Set, Map}
import scala.concurrent.Await
import scala.concurrent.duration._
import slick.driver.H2Driver.api._

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
// TODO: optimize this, a lot
// May need changes to DataStore interface

class DbDataStore extends DataStore {
  private val db = Database.forConfig("h2mem1")
  private val hashes = TableQuery[FileHashes]

  {
    val setup = DBIO.seq(
      //hashes.schema.drop,
      hashes.schema.create
    )
    Await.ready(db.run(setup), Duration.Inf)
  }

  override def addFile(file: String, hash: String): Unit = {
    removeFile(file)
    Await.ready(db.run(hashes.insertOrUpdate(None, file, hash)), Duration.Inf)
  }

  override def getAllDupesets: Map[String, Set[String]] = {
    val q = for {
      hash1 <- hashes.groupBy(_.hash).map({case (hash, hashes) => (hash, hashes.length)}).filter(_._2 > 1).map(_._1) // list of hashes with dupes
      (file_name, hash) <- hashes.map(x => (x.name, x.hash)) // map: file name -> hash
      if hash1 === hash
    }
      yield (hash, file_name)

    val result = Map[String, Set[String]]()
    for ((hash, name) <- Await.result(db.run(q.result), Duration.Inf))
      result.getOrElseUpdate(hash, Set[String]()) += name
    result
  }

  override def countDupes(file: String): Int = {
    val q = hashes.filter(_.name === file).map(_.hash)
    val hash = Await.result(db.run(q.result), Duration.Inf)(0)
    Await.result(db.run(hashes.filter(_.hash === hash).length.result), Duration.Inf)
  }

  override def removeFile(file: String) {
    Await.ready(db.run(hashes.filter(_.name === file).delete), Duration.Inf)
  }
}
