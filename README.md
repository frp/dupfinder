Duplicate finder
================

A simple tool to find duplicate files on the file system.

Usage
------

1. Download the last binary release here: https://github.com/frp/dupfinder/releases
2. Run it like this: `java -jar dupfinder-assembly-<version>.jar <directory> <min_size>`, where min_size is miminal file size in kilobytes to process

Git snapshot builds
--------------------

Snapshot builds are available as build artifacts on [CircleCI](https://circleci.com/gh/frp/dupfinder)

Build
-------
* Tested with sbt 0.13.8, uses Scala 2.11.6.
* To compile classes, run `sbt compile`.
* To prepare a "fat" jar (like the one in releases), run `sbt assembly`.
* To run a program, use `sbt run`.


Future plans
------------
1. Make output easier to read ~~(currently, it is just a dump of internal data structre)~~
  1. Improved: removed verbose debug logging, made output much prettier and easier to read
  2. TODO: make some kind of progress indicator
2. Provide a web or GUI version.
3. ~~Provide ready-to-download-and-use builds~~ DONE through GitHub releases
4. Create a daemon watching FS for changes constantly.


How it works
-----------
The application walks through a directory tree collecting data about files and their corresponding hashes filling a few data structures (or database in future, database version is present and covered by unit-tests, but it is unoptimized, much slower and intended for future versions with a daemon watching user's FS for changes constantly). Files are considered the same if their SHA1 hashes match (this is much faster than actually checking file contents, and the probability of an error is very small).
