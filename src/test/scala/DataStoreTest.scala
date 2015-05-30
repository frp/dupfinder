import org.apache.commons.codec.digest.DigestUtils.sha1Hex

import collection.mutable.Map
import collection.mutable.Set
import org.scalatest._

class DataStoreTest extends FlatSpec with Matchers {

  trait MemDataStore {
    val store = new MemoryDataStore

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

  "countOfDupes()" should "return count of dupes for a file name" in new MemDataStore {
    store.countDupes("fileWithNoDupes") should be(1)
    store.countDupes("fileWithADupe1") should be(2)
    store.countDupes("fileWithADupe2") should be(2)
    store.countDupes("fileWith2Dupes1") should be(3)
  }

  "getAllDupesets()" should "return a map: hash -> set of names of files" in new MemDataStore {
    store.getAllDupesets should be (Map[String, Set[String]](
      sha1Hex("data2") -> Set("fileWithADupe1", "fileWithADupe2"),
      sha1Hex("data3") -> Set("fileWith2Dupes1", "fileWith2Dupes2", "fileWith2Dupes3")
    ))
  }

  "removeFile()" should "remove file from dupe lists correctly" in new MemDataStore {
    store.removeFile("fileWithADupe1")
    store.getAllDupesets should be (Map[String, Set[String]](
      sha1Hex("data3") -> Set("fileWith2Dupes1", "fileWith2Dupes2", "fileWith2Dupes3")
    ))
  }

  "addFile()" should "process repeated files correctly" in new MemDataStore {
    store.addFile("fileWithNoDupes", sha1Hex("data4"))
    store.countDupes("fileWithNoDupes") should be(1)

    store.getAllDupesets should be (Map[String, Set[String]](
      sha1Hex("data2") -> Set("fileWithADupe1", "fileWithADupe2"),
      sha1Hex("data3") -> Set("fileWith2Dupes1", "fileWith2Dupes2", "fileWith2Dupes3")
    ))
  }

  "addFile()" should "process the situation when the updated version of a file is not a dupe anymore" in new MemDataStore {
    store.addFile("fileWithADupe1", sha1Hex("data5"))
    store.getAllDupesets should be (Map[String, Set[String]](
      sha1Hex("data3") -> Set("fileWith2Dupes1", "fileWith2Dupes2", "fileWith2Dupes3")
    ))
  }
}
