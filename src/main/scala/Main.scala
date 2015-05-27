import java.io.FileInputStream
import java.io.File
import org.apache.commons.codec.digest

/**
 * Created by roman on 26.05.15.
 */
object Main {
  def main(args: Array[String]): Unit = {
    val walker = new DirectoryWalker(new MemoryDataStore)
    walker.walk("/home/roman/myprograms")
    for (set <- walker.getAllDupesets)
      println(set)
  }
}
