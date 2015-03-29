scala-on-android playground
===========================

##Frameworks / tools used
- Android SDK Plugin documentation: https://github.com/pfn/android-sdk-plugin
- Macroid documentation: http://macroid.github.io 
- Execute REST API with Retrofit https://github.com/square/retrofit
- Async executing with Futures & UiActions (http://macroid.github.io/guide/UiActions.html)
- IntelliJ 14

##Running
- Make sure ANDROID_HOME & ANDROID_SDK_HOME are set SDK directory
- Make sure local.properties points to SDK directory
- sbt
- android:run
- ~android:run (for incremental)

##IDE
- Create link to ~/.android to SDK_HOME/.android (this fixes the tooling in IntelliJ)
- IntelliJ for best Scala support
    - To debug: 
        -  Attach process after startup in IntelliJ and set breakpoints
        -  Note: attach only possible after startup (so debug onCreate not possible)
- Android Studio for best Android API support
    - Deprecated Scala plugin needed

##HOWTOs

### How to combine layout.xml with Macroid DSL

Deserialize the layout file with the LayoutInflater

```Scala
//as class member
lazy val inflater = getApplicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
// as local variable in onCreate method
val xmlView = inflater.inflate(R.layout.test_layout, null) 
```

Wrap the view in an Ui object and add the xml view into view created with the Macroid DSL

```Scala
val view = l[LinearLayout](
    ...some elements ...
    , Ui(xmlView))
```

###How to add an event handlers to a widget which was part of the layout XML file (Macroid-style) 

Use a transformer to change the layout tree created with the Macroid DSL

Note: the Transformer immutable (the result of the Transformer is the updated view). 

```Scala
// create a reference to the widget
lazy val button = findView(TR.button)


// transform the existing layout build with the use of the Macroid DSL
val transformedView = view <~ Transformer {
  case button: Button if button.getId == TR.button_two.id => button <~ On.click {
    ... some code ...
  }
}

setContentView(getUi(transformedView))
```

###How to add an event handlers to a widget which was part of the layout XML file (vanilla Android)

```Scala
val button = xmlView.getRootView.findViewById(R.id.button).asInstanceOf[Button]

button.setOnClickListener(new OnClickListener {
  override def onClick(v: View) = {
    ...
  }
})
```

#General findings
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

#Open points to investigate
[ ] generic extension method for add event handlers without transformer / pattern matching code
[ ] checkout framework: Scaloid
[ ] checkout usage RX.Scala
[ ] checkout mixing layout XML with macroid
[ ] RoboElectric with Scala
