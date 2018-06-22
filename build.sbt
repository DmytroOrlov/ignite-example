import Dependencies._

lazy val `ignite-example` = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github.DmytroOrlov",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    libraryDependencies ++= Seq(
      "org.apache.ignite" % "ignite-core" % "2.5.0",
      "org.apache.ignite" % "ignite-indexing" % "2.5.0",
      scalaTest % Test,
      scalaCheck % Test
    )
  )
