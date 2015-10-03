lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    name := "url_shortener",
    version := "1.0",

    scalaVersion := "2.11.7",

    libraryDependencies ++= Seq(
      "com.twitter" %% "finagle-httpx" % "6.29.0",
      "com.twitter" %% "finagle-redis" % "6.29.0",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test,it",
      "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test"
    )
  )
