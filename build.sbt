import android.Dependencies.aar
import android.Keys._

android.Plugin.androidBuild

platformTarget in Android := "android-22"

name := "android-on-scala-playground"

scalaVersion := "2.11.6"

run <<= run in Android

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "jcenter" at "http://jcenter.bintray.com"
)

scalacOptions in (Compile, compile) ++=
  (dependencyClasspath in Compile).value.files.map("-P:wartremover:cp:" + _.toURI.toURL)

scalacOptions in (Compile, compile) ++= Seq(
  "-P:wartremover:traverser:macroid.warts.CheckUi"
)

libraryDependencies ++= Seq(
  aar("org.macroid" %% "macroid" % "2.0.0-M3"),
  aar("com.android.support" % "support-v4" % "20.0.0"),
  "com.squareup.retrofit" % "retrofit" % "1.9.0",
  compilerPlugin("org.brianmckenna" %% "wartremover" % "0.10")
)

useProguard in Android := true

proguardOptions in Android ++= Seq(
  "-ignorewarnings",
  "-keep class scala.Dynamic",
  "-keepattributes InnerClasses",
  "-dontwarn retrofit.**",
  "-keepattributes *Annotation*",
  "-keep class retrofit.** { *; }",
  "-keepclasseswithmembers class * { @retrofit.http.* <methods>; }",
  "-keepattributes Signature"
)
