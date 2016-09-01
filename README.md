#PAWL

This project contains one component:

- pawl: A project to aid with test automation.


##Downloading
The output jars are hosted in Maven Central:

    group: com.geeoz.pawl
    artifact: pawl

##Building the Library
This will build the library, publish it to a local artifactory repository, then resolve the dependencies of the project. 

###Requirements

- [Java JDK (1.8+)] [java]
- [SBT (0.13.12+)] [sbt]
- [PhantomJS (2.0.0+)] [phantom]

###Steps

1. Download the source code from git: `git clone git@bitbucket.org:geeoz/pawl.git`
2. Open a command line in the cloned directory: `cd ./pawl`
3. Now run the sbt to build the atom: `sbt it:build`

### Configuration
  - `.sbtopts` - configuration file with settings for SBT build 
  - `build.sbt` - sbt file with task definitions
  - `project/build.properties` - property file with SBT configuration
  - `project/build.sbt` - sbt file with task definitions for building build project
  - `project/Dependencies.scala` - scala file with module dependencies for the project
  - `project/Maven.scala` - scala file with publishing settings for the project
  - `project/plugin.sbt` - sbt file with plugin configurations

### SBT Tasks (build.sbt)

  - `sbt build` - action is used to clean, compile, test and package project
  - `sbt it:build` - action is used to clean, compile, test, integration test, static code analysis and package project
  - `sbt sca` - action is used to clean, compile, test and package project
  - `sbt publish-local` - action is used to publish project to a local Ivy repository
  - `sbt ~test-quick` - action is used to compile and test changed file in TDD mode
  - `sbt dependency-tree` - action is used to show dependency tree

## More Help

More assistance can be found in our documentation and our [developer hub] [dev-hub].

[dev-hub]: http://developer.geeoz.com "Geeoz Developer Hub"
[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html "Java"
[sbt]: http://www.scala-sbt.org "SBT"
[phantom]: http://phantomjs.org/ "PhantomJS"
