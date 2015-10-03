val PhantomVersion = "1.12.2"

lazy val root = (project in file("."))
  .settings(
    name := "url_shortener",
    version := "1.0",

    scalaVersion := "2.11.7",

    libraryDependencies ++= Seq(
      "com.twitter" %% "finagle-httpx" % "6.29.0",
      "com.twitter" %% "finagle-redis" % "6.29.0",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test"
    ),

    resolvers ++= Seq(
      Resolver.bintrayRepo("websudos", "oss-releases")
    )
  )
    