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
import com.typesafe.sbt.SbtPgp.autoImport._

object Publish {
  lazy val settings = Seq(
    resolvers := {
      val localMaven = Resolver.mavenLocal
      localMaven +: resolvers.value
    },
    credentials += Credentials(
      "Sonatype Nexus Repository Manager",
      "oss.sonatype.org",
      sys.env.getOrElse("SONATYPE_USER", ""),
      sys.env.getOrElse("SONATYPE_KEY", "")),
    pgpSecretRing := file("local.secring.asc"),
    pgpPublicRing := file("local.pubring.asc"),
    pgpPassphrase := Some(sys.env.getOrElse("PGP_PASSPHRASE", "").toCharArray),
    publishArtifact in Test := false,
    pomExtra :=
        <url>https://github.com/geeoz/pawl</url>

        <licenses>
          <license>
            <name>The Apache Software License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
            <distribution>repo</distribution>
          </license>
        </licenses>

        <developers>
          <developer>
            <id>alexander.voloshyn</id>
            <name>Alex Voloshyn</name>
            <email>alex.voloshyn@geeoz.com</email>
            <organization>Geeoz Software</organization>
            <organizationUrl>http://www.geeoz.com</organizationUrl>
            <roles>
              <role>Architect</role>
              <role>Developer</role>
            </roles>
            <timezone>+2</timezone>
          </developer>
          <developer>
            <id>mike.dolinin</id>
            <name>Mike Dolinin</name>
            <email>mike.dolinin@geeoz.com</email>
            <organization>Geeoz Software</organization>
            <organizationUrl>http://www.geeoz.com</organizationUrl>
            <roles>
              <role>Developer</role>
            </roles>
            <timezone>+2</timezone>
          </developer>
        </developers>

        <scm>
          <connection>scm:git:ssh://git@github.com/geeoz/pawl</connection>
          <developerConnection>scm:git:ssh://git@github.com/geeoz/pawl</developerConnection>
          <url>https://github.com/geeoz/pawl</url>
        </scm>
  )
}
