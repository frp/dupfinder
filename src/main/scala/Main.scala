/**
 * Created by roman on 26.05.15.
 */

object Main {
  def printUsage {
    println("Usage: dupfinder <directory> <min file size in kilobytes>")
  }

  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      printUsage
    }
    else {
      try {
        val walker = new DirectoryWalker(new MemoryDataStore, args(1).toInt * 1024)
        walker.walk(args(0))
        for (set <- walker.getAllDupesets)
          println(set)
      }
      catch {
        case e: NumberFormatException => println("Error: second argument should be a number")
      }
    }
  }
}
