/**
 * Created by roman on 26.05.15.
 */

import java.io.{FileInputStream, File}
import org.apache.commons.codec.digest.DigestUtils

class DirectoryWalker(storage: DataStore) {

  def walk(file: String): Unit = {
    walk(new File(file))
  }

  def walk(file: File) {
    println("Walking " + file.getAbsolutePath)
    if (file.isDirectory) {
      for (f <- file.listFiles()) walk(f)
    }
    else if (file.isFile) {
      val h = hash(file)
      storage.addFile(file.getAbsolutePath, h)
    }
  }

  private def hash(file: File) = {
    val fis = new FileInputStream(file)
    val result = DigestUtils.sha1Hex(fis)
    fis.close()
    result
  }

  def countDupes(f: File) = storage.countDupes(f.getAbsolutePath)

  def getAllDupesets = storage.getAllDupesets
}
