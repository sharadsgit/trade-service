val akkaHttpVersion = "10.6.3"
val akkaVersion = "2.9.3"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

//Resolver.typesafeRepo("releases")
//libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8"
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion  := "2.13.10",
      scalacOptions += "-Ydebug",
      organization  := "com.example"
    )),
    name := "akka-http-trade-service",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion,

      "org.scalactic" %% "scalactic" % "3.0.8",
      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test,

      "org.json4s" %% "json4s-native" % "3.6.7"

)
  )


