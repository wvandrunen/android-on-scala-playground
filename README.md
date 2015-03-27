scala-on-android playground
===========================

Stuff:
- *Android SDK Plugin* documentation: https://github.com/pfn/android-sdk-plugin
- *Macroid* documentation: http://macroid.github.io 
- Execute REST API with Retrofit https://github.com/square/retrofit
- Async executing with Futures & UiActions (http://macroid.github.io/guide/UiActions.html)

Running:
- Make sure ANDROID_HOME & ANDROID_SDK_HOME are set SDK directory
- Make sure local.properties points to SDK directory
- sbt
- android:run
- ~android:run (for incremental)

IDE:
- Create link to ~/.android to SDK_HOME/.android (this fixes the tooling in IntelliJ)
- IntelliJ for best Scala support
    - To debug: 
        -  Attach process after startup in IntelliJ and set breakpoints
        -  Note: attach only possible after startup (so debug onCreate not possible)
- Android Studio for best Android API support
    - Deprecated Scala plugin needed

General findings:
- use local.properties to point to the correct SDK
- Scalaz runs in 65K+ methods error with Dex / Proguard
- proguard
    - proguard-sbt.txt is overwrite every buildcycle (so do not edit!)
    - Options: make sure every config item is a oneliner
    ```Scala
    List("keepclasseswithmembers class * { @retrofit.http.* <methods>; }")
    ```
    instead of 
    ```Scala
    List("keepclasseswithmembers class * {",
         "@retrofit.http.* <methods>; ",
         "}")
    ```

Open:
- checkout framework: Scaloid
- checkout usage RX.Scala
- checkout mixing layout XML with macroid
- RoboElectric with Scala
