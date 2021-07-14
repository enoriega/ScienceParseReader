name := "ScienceParseReader"

version := "0.1"

scalaVersion := "2.12.4"

idePackagePrefix := Some("org.clulab.scienceparse")

val json4sVersion = "3.5.2"

//EclipseKeys.withSource := true

libraryDependencies ++= {
  val procVer = "7.5.1"

  Seq(
    "ai.lum"        %% "common"                   % "0.0.10",
//    "ai.lum"        %% "regextools"               % "0.1.0-SNAPSHOT",
    "com.lihaoyi"   %% "ujson"                    % "0.7.0",
//    "com.lihaoyi"   %% "requests"                 % "0.5.1",
//    "com.lihaoyi"   %% "upickle"                  % "0.7.0",
    "com.lihaoyi"   %% "ujson-json4s"             % "0.7.0",
    "org.scalatest" %% "scalatest"                % "3.0.4" % "test",
    "com.typesafe"  %  "config"                   % "1.3.1",
    "org.json4s"    %%  "json4s-core"             % json4sVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
    "org.apache.commons" % "commons-text" % "1.6",
//    "com.typesafe.play" %% "play-json" % "2.7.0",
    "org.json4s" %% "json4s-jackson" % "4.0.1",
//    "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
  )
}
