/**
 * Created by roman on 26.05.15.
 */

import slick.driver.H2Driver.api._

object Main {
  def main(args: Array[String]): Unit = {
    val walker = new DirectoryWalker(new DbDataStore(Database.forConfig("h2main")))
    walker.walk("/home/roman/myprograms")
    for (set <- walker.getAllDupesets)
      println(set)
  }
}
