name := """docetentacao"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaJpa,
  cache,
  javaWs,
  "com.amazonaws" % "aws-java-sdk" % "1.9.30",
  "javax.mail" % "mail" % "1.4.1",
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.0",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "org.hibernate" % "hibernate-entitymanager" % "4.3.8.Final",
  "javax.mail" % "mail" % "1.4.1",
  "commons-io" % "commons-io" % "2.3",
  "com.typesafe.play" %% "play-mailer" % "2.4.0",
  "org.apache.commons" % "commons-email" % "1.3.3",
  "org.imgscalr" % "imgscalr-lib" % "4.2"
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
val _ = initialize.value
if (sys.props("java.specification.version") != "1.8")
sys.error("Java 8 is required for this project.")
}
