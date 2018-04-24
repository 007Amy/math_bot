import sbt.Keys._

lazy val GatlingTest = config("gatling") extend Test

scalaVersion := "2.11.11"

libraryDependencies += "com.netaporter" %% "scala-uri" % "0.4.14"
libraryDependencies += "net.codingwell" %% "scala-guice" % "4.1.0"

// test dependencies
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.5" % Test
libraryDependencies += "io.gatling" % "gatling-test-framework" % "2.2.5" % Test
libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"
libraryDependencies += filters
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.12.5-play25"
libraryDependencies += "com.github.pdorobisz" %% "math-expression-evaluator" % "1.0.0"
resolvers += "dl-john-ky" at "http://dl.john-ky.io/maven/releases"
libraryDependencies += "io.john-ky" %% "hashids-scala" % "1.1.2-2974446"


// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala, GatlingPlugin)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(
    name := """MATH_BOT""",
    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation",
    watchSources ++= (baseDirectory.value / "public/ui" ** "*").get
  )

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/index.html
lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin).
  settings(
    paradoxProperties += ("download_url" -> "https://example.lightbend.com/v1/download/play-rest-api")
  )

// Pre - Prod deploy
herokuAppName in Compile := HerokuDeployKeys.NAME
herokuJdkVersion in Compile := "1.8"
herokuConfigVars in Compile := HerokuDeployKeys.CONFIG_VARS
herokuProcessTypes in Compile := HerokuDeployKeys.PROCESS_TYPES
