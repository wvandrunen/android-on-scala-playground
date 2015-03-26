package com.example.macroid.starter

import java.net.URL

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, View}
import android.widget.{Button, LinearLayout, TextView}
import com.stackmob.newman._
import com.stackmob.newman.dsl._
import scala.concurrent._
import scala.concurrent.duration._

// import macroid stuff
import macroid.FullDsl._
import macroid._
import macroid.contrib._

import scala.concurrent.ExecutionContext.Implicits.global

// define our helpers in a mixable trait
trait Styles {
  // sets text, large font size and a long click handler
  def caption(cap: String)(implicit ctx: AppContext): Tweak[TextView] =
    text(cap) + TextTweaks.large + On.longClick {
      (toast("I’m a caption") <~ gravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL) <~ fry) ~
      Ui(true)
    }
}

//object RestAdapterFactory {
//
//  case class MyIp(origin: String)
//
//  trait HttpBinService {
//    @GET("/ip")
//    def getMyIp: MyIp
//  }
//
//  def get(): HttpBinService = {
//    val restAdapter = new RestAdapter.Builder()
//      .setEndpoint("https://httpbin.org")
//      .build()
//
//    val clazz = classOf[HttpBinService]
//
//    val annotations = clazz.getMethods.toList.map(method => method.getName -> method.getDeclaredAnnotations)
//
//    restAdapter.create[HttpBinService](clazz)
//  }
//}

object RestClient {
  implicit val httpClient = new ApacheHttpClient

  def getMyIp: String = {
    val url = new URL("http://google.com")

    val response = Await.result(GET(url).apply, 5.second)

    response.bodyString
  }
}

// mix in Contexts for Activity
class MainActivity extends Activity with Styles with Contexts[Activity] {
  // prepare a variable to hold our text view
  var cap = slot[TextView]

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    // this will be a linear layout

    val view = l[LinearLayout](
      // a text view
      w[TextView] <~
        // use our helper
        caption("Howdy dit is een test") <~
        // assign to cap
        wire(cap),

      // a button
      w[Button] <~
        // set text
        text("Click me!") <~
        // set layout params (LinearLayout.LayoutParams will be used)
        layoutParams[LinearLayout](MATCH_PARENT, WRAP_CONTENT) <~
        // set click handler
        On.click {
          // with <~~ we can apply snails like `delay`
          // tweaks coming after them will wait till they finish
          cap <~ text("Button clicked!") <~~ delay(1000) <~ text(RestClient.getMyIp)
        }
    ) <~
      // match layout orientation to screen orientation
      (portrait ? vertical | horizontal) <~ Transformer {
        // here we set a padding of 4 dp for all inner views
        case x: View ⇒ x <~ padding(all = 4 dp)
      }

    setContentView(getUi(view))
  }
}
