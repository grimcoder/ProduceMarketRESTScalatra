organization := "com.github.grimcoder"

name := "scalatra-producemarketrest"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.3"

seq(webSettings :_*)

classpathTypes ~= (_ + "orbit")

libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.2.0",
  "com.novus" %% "salat" % "1.9.7",
  "org.mongodb" %% "casbah" % "2.8.2",
  "org.scalatra" % "scalatra" % "2.1.1",
  "org.scalatra" % "scalatra-scalate" % "2.1.1",
  "org.scalatra" % "scalatra-scalatest" % "2.1.1" % "test",
  "org.scalatra" % "scalatra-lift-json" % "2.1.1",
  "ch.qos.logback" % "logback-classic" % "1.0.7" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "8.1.6.v20120903" % "container",
  "org.eclipse.jetty" % "test-jetty-servlet" % "8.1.6.v20120903" % "test",
  "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar"))
)

port in container.Configuration := 3001
