import Dependencies._

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "zio-stream-consecutive",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-streams" % "1.0.12",
      "dev.zio" %% "zio" % "1.0.12",
      "dev.zio" %% "zio-logging-slf4j" % "0.5.12",
      "dev.zio" %% "zio-test" % "1.0.12" % "test",
      "dev.zio" %% "zio-test-sbt" % "1.0.12" % "test"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
