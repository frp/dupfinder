name := "dupfinder"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies += "commons-codec" % "commons-codec" % "1.10"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "org.slf4j" % "slf4j-nop" % "1.7.12"
)

libraryDependencies += "com.h2database" % "h2" % "1.4.187"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test"
