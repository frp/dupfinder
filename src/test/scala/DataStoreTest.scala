import org.apache.commons.codec.digest.DigestUtils.sha1Hex

import collection.mutable.Map
import collection.mutable.Set
import org.scalatest._

abstract class DataStoreTest extends FlatSpec with Matchers {

  def getStore: DataStore

  trait DataStoreFixture {
    val store = getStore

    val data = Array(
      ("fileWithNoDupes", sha1Hex("data1")),
      ("fileWithADupe1", sha1Hex("data2")),
      ("fileWithADupe2", sha1Hex("data2")),
      ("fileWith2Dupes1", sha1Hex("data3")),
      ("fileWith2Dupes2", sha1Hex("data3")),
      ("fileWith2Dupes3", sha1Hex("data3"))
    )

    for ((file, hash) <- data)
      store.addFile(file, hash)
  }

  "countOfDupes()" should "return count of dupes for a file name" in new DataStoreFixture {
    store.countDupes("fileWithNoDupes") should be(1)
    store.countDupes("fileWithADupe1") should be(2)
    store.countDupes("fileWithADupe2") should be(2)
    store.countDupes("fileWith2Dupes1") should be(3)
  }

  "getAllDupesets()" should "return a map: hash -> set of names of files" in new DataStoreFixture {
    store.getAllDupesets should be(Map[String, Set[String]](
      sha1Hex("data2") -> Set("fileWithADupe1", "fileWithADupe2"),
      sha1Hex("data3") -> Set("fileWith2Dupes1", "fileWith2Dupes2", "fileWith2Dupes3")
    ))
  }

  "removeFile()" should "remove file from dupe lists correctly" in new DataStoreFixture {
    store.removeFile("fileWithADupe1")
    store.getAllDupesets should be(Map[String, Set[String]](
      sha1Hex("data3") -> Set("fileWith2Dupes1", "fileWith2Dupes2", "fileWith2Dupes3")
    ))
  }

  "addFile()" should "process repeated files correctly" in new DataStoreFixture {
    store.addFile("fileWithNoDupes", sha1Hex("data4"))
    store.countDupes("fileWithNoDupes") should be(1)

    store.getAllDupesets should be(Map[String, Set[String]](
      sha1Hex("data2") -> Set("fileWithADupe1", "fileWithADupe2"),
      sha1Hex("data3") -> Set("fileWith2Dupes1", "fileWith2Dupes2", "fileWith2Dupes3")
    ))
  }

  "addFile()" should "process the situation when the updated version of a file is not a dupe anymore" in new DataStoreFixture {
    store.addFile("fileWithADupe1", sha1Hex("data5"))
    store.getAllDupesets should be(Map[String, Set[String]](
      sha1Hex("data3") -> Set("fileWith2Dupes1", "fileWith2Dupes2", "fileWith2Dupes3")
    ))
  }
}

class MemoryDataStoreTest extends DataStoreTest {
  def getStore = new MemoryDataStore
}

class DbDataStoretest extends DataStoreTest {
  def getStore = new DbDataStore
}