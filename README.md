Duplicate finder
================

A simple tool to find duplicate files on the file system.

Usage
-------
1. Run `sbt`
2. Type `run <directory> <minimum file size in kilobytes>`
3. "Fat" ("one-click") jar packages can be prepared using a command `assembly` in `sbt` shell.


Future plans
------------
1. Make output easier to read (currently, it is just a dump of internal data structre)
2. Provide a web or GUI version.
3. Provide ready-to-download-and-use builds
4. Create a daemon watching FS for changes constantly.


How it works
-----------
The application walks through a directory tree collecting data about files and their corresponding hashes filling a few data structures (or database in future, database version is present and covered by unit-tests, but it is unoptimized, much slower and intended for future versions with a daemon watching user's FS for changes constantly). Files are considered the same if their SHA1 hashes match (this is much faster than actually checking file contents, and the probability of an error is very small).
