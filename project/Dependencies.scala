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

import sbt.Keys._
import sbt._

object Dependencies {
  val Scala = Seq(
    scalaVersion := "2.11.8"
  )

  // Typesafe Config Factory
  val configVersion         = "1.3.0"         // ApacheV2
  // Hamcrest matchers
  val hamcrestVersion       = "1.3"           // EPL-1.0
  // ScalaTest
  val scalaTestVersion      = "3.0.0-SNAP6"   // ApacheV2
  // Browser automation framework
  val seleniumVersion       = "3.0.1"        // ApacheV2

  object Compile {
    val config              = "com.typesafe" % "config" % configVersion
    val hamcrest            = "org.hamcrest" % "hamcrest-all" % hamcrestVersion
    val scalaTest           = "org.scalatest" %% "scalatest" % scalaTestVersion
    val selenium            = "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion

    object Test {
    }

    object It {
    }
  }

  import Compile._

  val l = libraryDependencies

  // Dependencies for pawl-scalatest module
  val pawlScalaTest = l ++=
    Seq(config, hamcrest, scalaTest, selenium)
}
