name := "BioSignalServer"

//Global / onChangedBuildSource := ReloadOnSourceChanges

scalaVersion := "2.13.3"
	
val zioVersion = "1.0.1"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"               % zioVersion,
  "dev.zio" %% "zio-nio" % "1.0.0-RC9",
  "dev.zio" %% "zio-test"          % zioVersion % "test",
  "dev.zio" %% "zio-test-sbt"      % zioVersion % "test"
  //"dev.zio" %% "zio-test-magnolia" % zioVersion % "test"
)
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
