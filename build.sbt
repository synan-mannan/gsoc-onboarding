val scala3Version = "3.5.2"
val scalajsDomVersion = "2.8.0"
val calicoVersion = "0.2.3"
val http4sVersion = "0.23.33"
val circeVersion = "0.14.15"
val http4sDomVersion = "0.2.12"

lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "gsoc-onboarding",
    scalaJSUseMainModuleInitializer := true,
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    libraryDependencies ++= List(
      "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
      "com.armanbilge" %%% "calico" % calicoVersion,
      "org.http4s" %%% "http4s-circe" % http4sVersion,
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion,
      "org.http4s" %%% "http4s-dom" % http4sDomVersion
    )
  )
