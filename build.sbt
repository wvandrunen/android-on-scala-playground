import android.Keys._
import android.Dependencies.{LibraryDependency, aar}

android.Plugin.androidBuild

platformTarget in Android := "android-19"

name := "macroid-starter"

scalaVersion := "2.11.1"

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
  aar("com.google.android.gms" % "play-services" % "4.0.30"),
  aar("com.android.support" % "support-v4" % "20.0.0"),
  "com.stackmob" % "newman_2.10" % "1.3.5",
  compilerPlugin("org.brianmckenna" %% "wartremover" % "0.10")
)

proguardScala in Android := false

proguardOptions in Android ++= Seq(
  "-ignorewarnings",
  "-keep class scala.Dynamic"
//  "-keepattributes *Annotation*",
//  "-keep class retrofit.** { *; }",
//  "-keepclasseswithmembers class * {",
//  "@retrofit.http.* <methods>; }",
//  "-keepattributes Signature"
)
