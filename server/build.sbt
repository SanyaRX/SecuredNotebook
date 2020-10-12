name := "server"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % "2.5.4",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.12.v20180830",
)