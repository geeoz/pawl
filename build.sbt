/*
 * Copyright 2015 Geeoz Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import scala.xml.XML

val mainDirectory = settingKey[File]("Main base directory of the project.")
val build = taskKey[Unit]("Project full build task.")
val sca = taskKey[Unit]("Project static code analysis task.")

lazy val buildSettings = Dependencies.Scala ++ graphSettings ++
  filterSettings ++
  findbugsSettings ++
  PmdPlugin.projectSettings ++
  Publish.settings ++ Seq(
  organization := "com.geeoz.pawl",
  organizationName := "Geeoz Software",
  organizationHomepage := Some(new URL("http://www.geeoz.com")),
  scalacOptions ++= Seq("-deprecation", "-feature",
    "-language:implicitConversions", "-language:postfixOps",
    "-language:reflectiveCalls", "-language:experimental.macros"),
  javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.8", "-target", "1.8",
    "-Xlint:unchecked", "-Xlint:deprecation"),
  updateOptions := updateOptions.value.withCachedResolution(true),
  logLevel := Level.Info,
  jcheckStyleConfig := mainDirectory.value.getPath + "/checkstyle.xml",
  pmdRuleSet := mainDirectory.value.getPath + "/pmd-ruleset.xml",
  findbugsExcludeFilters := Some(XML.loadFile(mainDirectory.value / "findbugs-exclude.xml")),
  sca := {},
  sca <<= sca dependsOn(
    jcheckStyle in Compile, scalastyle.in(Compile).toTask(""), scapegoat in Scapegoat, findbugs in Compile, pmd),
  build := {},
  build <<= build dependsOn(clean in Compile, compile in Compile, test in Test, publishLocal in Compile),
  build in IntegrationTest <<= (build in IntegrationTest) dependsOn(
    clean in Compile, compile in Compile, test in Test, sca, publishLocal in IntegrationTest)
)

lazy val pawl = (project in file(".")).
  aggregate(
    `pawl-scalatest`).
  settings(buildSettings: _*).
  settings(
    name := "pawl",
    description := "A project to aid with test automation.",
    mainDirectory := baseDirectory.value,
    findbugs in Compile := {},
    pmd := {}
  )

lazy val `pawl-scalatest` = (project in file("pawl-scalatest")).
  configs(IntegrationTest).
  enablePlugins(PawlPlugin).
  settings(buildSettings: _*).
  settings(
    name := "pawl-scalatest",
    description := "Test suite for performing integration tests.",
    mainDirectory := baseDirectory.value.getParentFile,
    Dependencies.pawlScalaTest,
    // FIXME update Scalastyle plugin
    sca := {},
    sca <<= sca dependsOn(
      jcheckStyle in Compile, scapegoat in Scapegoat, findbugs in Compile, pmd)
  )
