// Name of the project
name := "Delta"

// Project version
version := "1.6.1"

// Version of Scala used by the project
scalaVersion := "2.11.8"

// Adding dependency on Apache Spark library
libraryDependencies ++= Seq(
  "com.panayotis" % "javaplot" % "0.5.0",
  "org.scala-lang" % "scala-reflect" % "2.11.8",
  "org.scalafx" %% "scalafx" % "8.0.144-R12",
  "org.apache.commons" % "commons-math3" % "3.6.1",
  "org.apache.spark" %% "spark-core" % "2.3.2",
  "org.apache.spark" %% "spark-mllib" % "2.3.2",
  "org.apache.spark" %% "spark-streaming" % "2.3.2" % "provided",
  "org.apache.spark" %% "spark-mllib-local" % "2.3.2"
)
// Should there be a need for machine learning the below dependency should be added to the above list
// "org.apache.spark" %% "spark-mllib" % "1.6.0-typesafe-001"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-Xcheckinit", "-encoding", "utf8", "-feature")

// Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization
// Uncomment below line if using scalaFX
//fork := true
