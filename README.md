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

- [Java JDK (1.7+)] [java]
- [Apache Maven (3.0.4+)] [apache-maven]

###Steps

1. Download the source code from git: `git clone git@bitbucket.org:geeoz/pawl.git`

2. Open a command line in the cloned directory: `cd ./pawl`

3. Now run the maven to build the travelport-api: `mvn clean install`
    - The library has now been built, published to a local artifactory repository, the app has had its dependencies resolved.


## More Help

More assistance can be found on our [developer hub] [dev-hub].

[dev-hub]: https://developer.geeoz.com "Geeoz Developer Hub"
[git-project]: https://bitbucket.org/geeoz/travelport-api.git "travelport-api project"
[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html "Java"
[apache-maven]: http://maven.apache.org/download.cgi "Apache Maven"
