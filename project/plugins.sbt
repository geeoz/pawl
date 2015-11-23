// Visualize your project's dependencies.
// sbt dependency-graph - shows an ASCII graph of the project's dependencies on the sbt console
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")

// This plugin scans your resources (e.g src/main/resources, src/test/resources) for variables
// (surrounded by ${ and }) and replaces them with values which can come from the system properties,
// your project properties and filter resources.
addSbtPlugin("com.github.sdb" % "xsbt-filter" % "0.4")

// This plugin generates all necessary utility classes for PAWL Framework.
addSbtPlugin("com.geeoz.sbt" % "sbt-pawl" % "0.2.2")

// -- CODE STYLE --
// Sbt plugin for checking Scala code styles with checkstyle.
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")

// Sbt plugin that integrates the scala static code analysis library.
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.0.0")

// Sbt findbugs plugin.
addSbtPlugin("de.johoop" % "findbugs4sbt" % "1.4.0")

// PMD is a source code analyzer. It finds common programming flaws like unused variables,
// empty catch blocks, unnecessary object creation, and so forth.
addSbtPlugin("com.geeoz.sbt" % "sbt-pmd" % "0.1.0")

// A sbt plugin for checking Java code styles with checkstyle.
addSbtPlugin("org.xerial.sbt" % "sbt-jcheckstyle" % "0.1.2")

// -- RELEASE --
// A sbt plugin for publishing your project to the Maven central repository through the REST API
// of Sonatype Nexus.
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.5.0")

// The sbt-pgp plugin provides PGP signing for SBT.
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

// This sbt plugin provides a customizable release process that you can add to your project.
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.1")
