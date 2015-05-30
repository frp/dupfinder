import scala.collection.mutable.{Set, Map}

/**
 * Created by roman on 26.05.15.
 */
class MemoryDataStore extends DataStore {
  private val hashToFiles = Map[String, Set[String]]()
  private val fileToHash = Map[String, String]()
  private val hashCount = Map[String, Int]()
  private val hashesWithDupes = Set[String]()

  def removeFile(file: String) {
    val hash = fileToHash(file)

    fileToHash -= file
    hashCount(hash) -= 1

    if (hashCount(hash) == 1)
      hashesWithDupes -= hash

    hashToFiles(hash) -= file
  }

  override def addFile(file: String, hash: String) {
    if (fileToHash.contains(file)) {
      removeFile(file)
    }

    fileToHash += file -> hash
    hashToFiles.get(hash) match {
      case Some(set) => set += file
      case None => hashToFiles += hash -> Set(file)
    }
    hashCount(hash) = hashCount.getOrElseUpdate(hash, 0) + 1
    if (hashCount(hash) == 2)
      hashesWithDupes += hash
  }

  override def countDupes(file: String) = fileToHash.get(file) match {
    case None => 0
    case Some(h) => hashCount(h)
  }

  override def getAllDupesets = {
    val result = Map[String, Set[String]]()
    for (h <- hashesWithDupes)
      result += h -> hashToFiles(h)
    result
  }
}
