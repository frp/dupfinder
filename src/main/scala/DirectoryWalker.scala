/**
 * Created by roman on 26.05.15.
 */

import java.io.{FileInputStream, File}
import org.apache.commons.codec.digest.DigestUtils

class DirectoryWalker(storage: DataStore, minFileSize: Long) {

  def walk(file: String): Unit = {
    walk(new File(file))
  }

  def walk(file: File) {
    println("Walking " + file.getAbsolutePath)

    // process file only if it is not a symlink
    // this is needed in order to avoid possible loops
    if (file.getAbsolutePath == file.getCanonicalPath) {
      if (file.isDirectory) {
        for (f <- file.listFiles()) walk(f)
      }
      else if (file.isFile && file.length() >= minFileSize) {
        val h = hash(file)
        storage.addFile(file.getAbsolutePath, h)
      }
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
