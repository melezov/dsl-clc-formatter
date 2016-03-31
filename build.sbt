val NGSNexus     = "NGS Nexus"     at "http://ngs.hr/nexus/content/groups/public/"
val NGSReleases  = "NGS Releases"  at "http://ngs.hr/nexus/content/repositories/releases/"
val NGSSnapshots = "NGS Snapshots" at "http://ngs.hr/nexus/content/repositories/snapshots/"

lazy val interface = (
  project in file("formatter-interface")
  settings(commonSettings)
  settings(
    name := "DSL-CLC Formatter Interface"
  )
)

lazy val languageCSharp = (
  project in file("formatter-language-csharp")
  settings(commonSettings)
  settings(
    name := "DSL-CLC Formatter Language CSharp"
  )
) dependsOn(interface)

lazy val languageJava = (
  project in file("formatter-language-java")
  settings(commonSettings)
  settings(
    name := "DSL-CLC Formatter Language Java"
  , libraryDependencies ++= Seq(
      "org.eclipse.equinox" % "common" % "3.6.200.v20130402-1505"
    , "org.eclipse.jdt" % "core" % "3.10.0.v20140902-0626"
    , "org.eclipse" % "jface" % "3.10.1.v20140813-1009"
    , "org.eclipse" % "text" % "3.5.300.v20130515-1451"
    )
  )
) dependsOn(interface)

lazy val languagePHP = (
  project in file("formatter-language-php")
  settings(commonSettings)
  settings(
    name := "DSL-CLC Formatter Language PHP"
  )
) dependsOn(interface)

lazy val languageScala = (
  project in file("formatter-language-scala")
  settings(commonSettings)
  settings(
    name := "DSL-CLC Formatter Language Scala"
  , autoScalaLibrary := true
  , crossPaths := true
  , libraryDependencies += "org.scalariform" %% "scalariform" % "0.1.8"
  )
) dependsOn(interface)

lazy val languageSQL = (
  project in file("formatter-language-sql")
  settings(commonSettings)
  settings(
    name := "DSL-CLC Formatter Language SQL"
  )
) dependsOn(interface)

// ----------------------------------------------------------------------------

lazy val cli = (
  project in file("formatter-cli")
  settings(commonSettings)
  settings(
    name := "DSL-CLC Formatter"
  , assemblyJarName in assembly := s"dsl-clc-formatter-${version.value}.jar"
  , mainClass in assembly := Some("com.dslplatform.compiler.client.formatter.Main")
  )
) dependsOn(interface)

lazy val root = (
  project in file(".")
  settings(commonSettings)
  aggregate(
    interface
  , languageCSharp
  , languageJava
  , languagePHP
  , languageScala
  , languageSQL
  , cli
  )
)

lazy val commonSettings = Seq(
  organization := "com.dslplatform.formatter"
, autoScalaLibrary := false
, crossPaths := false
, javacOptions in doc := Seq(
    "-encoding", "UTF-8"
  , "-source", "1.6"
  )
, javacOptions in (Compile, compile) := (javacOptions in doc).value ++ Seq(
    "-target", "1.6"
  , "-deprecation"
  , "-Xlint:all"
  ) ++ (sys.env.get("JAVA6_HOME") match {
    case Some(java6Home) => Seq("-bootclasspath", java6Home + "/lib/rt.jar")
    case _ => Nil
  })
, scalaVersion := "2.11.8"
, scalacOptions := Seq(
    "-deprecation"
  , "-encoding", "UTF-8"
  , "-feature"
  , "-language:existentials"
  , "-language:implicitConversions"
  , "-language:postfixOps"
  , "-language:reflectiveCalls"
  , "-optimise"
  , "-unchecked"
  , "-Xcheckinit"
  , "-Xlint"
  , "-Xmax-classfile-name", "72"
  , "-Xno-forwarders"
  , "-Xverify"
  , "-Yclosure-elim"
  , "-Ydead-code"
  , "-Yinline-warnings"
  , "-Yinline"
  , "-Yrepl-sync"
  , "-Ywarn-adapted-args"
  , "-Ywarn-dead-code"
  , "-Ywarn-inaccessible"
  , "-Ywarn-nullary-override"
  , "-Ywarn-nullary-unit"
  , "-Ywarn-numeric-widen"
  )
, resolvers := Seq(NGSNexus)
, publishTo := Some(if (isSnapshot.value) NGSSnapshots else NGSReleases)
, credentials ++= {
    val creds = Path.userHome / ".config" / "dsl-clc-formatter" / "nexus.config"
    if (creds.exists) Seq(Credentials(creds)) else Nil
  }
)
