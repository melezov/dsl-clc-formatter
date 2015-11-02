import sbt._
import Keys._

trait Default {
  private val NGSNexus     = "NGS Nexus"     at "http://ngs.hr/nexus/content/groups/public/"
  private val NGSReleases  = "NGS Releases"  at "http://ngs.hr/nexus/content/repositories/releases/"
  private val NGSSnapshots = "NGS Snapshots" at "http://ngs.hr/nexus/content/repositories/snapshots/"

  lazy val defaultSettings =
    net.virtualvoid.sbt.graph.Plugin.graphSettings ++ Seq(
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
    , crossScalaVersions := Seq("2.11.7", "2.10.6")
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
    , publishTo := Some(if (version.value endsWith "-SNAPSHOT") NGSSnapshots else NGSReleases)
    , credentials ++= {
        val creds = Path.userHome / ".config" / "dsl-clc-formatter" / "nexus.config"
        if (creds.exists) Some(Credentials(creds)) else None
      }.toSeq
    )
}
