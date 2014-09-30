import AssemblyKeys._

organization := "com.dslplatform"

name := "dsl-clc-formatter"

version := "0.1.0"

scalaVersion := "2.11.2"

crossPaths := false

resolvers += "NGS 3rd Party" at "http://ngs.hr/nexus/content/repositories/thirdparty/"

libraryDependencies ++= Seq(
  "com.danieltrinh" %% "scalariform" % "0.1.5"
, "org.eclipse.equinox" % "common" % "3.6.200.v20130402-1505"
, "org.eclipse.jdt" % "core" % "3.10.0.v20140902-0626"
, "org.eclipse" % "jface" % "3.10.1.v20140813-1009"
, "org.eclipse" % "text" % "3.5.300.v20130515-1451"
, "junit" % "junit" % "4.11" % "test"
)

scalacOptions := Seq(
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
, "-Yconst-opt"
, "-Ydead-code"
, "-Yinline-warnings"
, "-Yinline"
, "-Yrepl-sync"
, "-Ywarn-adapted-args"
, "-Ywarn-dead-code"
, "-Ywarn-inaccessible"
, "-Ywarn-infer-any"
, "-Ywarn-nullary-override"
, "-Ywarn-nullary-unit"
, "-Ywarn-numeric-widen"
, "-Ywarn-unused"
)

javacOptions := Seq(
  "-deprecation"
, "-encoding", "UTF-8"
, "-source", "1.6"
, "-target", "1.6"
, "-Xlint:unchecked"
) ++ (sys.env.get("JDK16_HOME") match {
  case Some(jdk16Home) => Seq("-bootclasspath", jdk16Home + "/jre/lib/rt.jar")
  case _ => Nil
})

graphSettings

assemblySettings

jarName in assembly := "dsl-clc-formatter.jar"

mainClass in assembly := Some("com.dslplatform.compiler.client.formatter.Main")

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case x if x matches ".*\\.(SF|RSA|html)" => MergeStrategy.discard
  case _ => MergeStrategy.last
}}

ideaExcludeFolders ++= Seq(".idea", ".idea_modules")
