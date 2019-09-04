val scala_version = "2.13.0"

lazy val root = (project in file(".")).settings(
  name := "reinforcement-learning-tetris",
  version := "1.0",
  scalaVersion := scala_version,
  mainClass in Compile := Some("com.uhu.cesar.tetris.app.Main"),
  mainClass in assembly := Some("com.uhu.cesar.tetris.app.Main"),
  assemblyOutputPath in assembly := new File("jars/rl-tetris-heights-board.jar")
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.8" % Test
)
