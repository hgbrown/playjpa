name := "playjpa"

version := "1.0"

lazy val `playjpa` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  evolutions,
  javaJdbc,
  javaJpa,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "org.hibernate" % "hibernate-entitymanager" % "4.3.10.Final"

)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")