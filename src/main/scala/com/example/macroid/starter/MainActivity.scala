package com.example.macroid.starter

import android.app.Activity
import android.os.Bundle
import android.view.{Window, Gravity}
import android.widget._
import retrofit.RestAdapter
import retrofit.http.GET

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// import macroid stuff
import macroid.FullDsl._
import macroid._
import macroid.contrib._

// define our helpers in a mixable trait
trait Styles {
  // sets text, large font size and a long click handler
  def caption(cap: String)(implicit ctx: AppContext): Tweak[TextView] =
    text(cap) + TextTweaks.large + On.longClick {
      (toast("I’m a caption") <~ gravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL) <~ fry) ~
      Ui(true)
    }
}

object RestAdapterFactory {

  case class MyIp(origin: String)

  trait HttpBinService {
    @GET("/ip")
    def getMyIp: MyIp
  }

  def get(): HttpBinService = {
    val restAdapter = new RestAdapter.Builder()
      .setEndpoint("https://httpbin.org")
      .build()

    restAdapter.create[HttpBinService](classOf[HttpBinService])
  }
}

class MainActivity extends Activity with Contexts[Activity] {
  var output = slot[TextView]
  lazy val service = RestAdapterFactory.get()

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)

    val view = l[LinearLayout](
      l[LinearLayout](
        w[Button] <~ text("Get My IP") <~ On.click {
          output <~ search()
        },
        w[Button] <~ text("Change text with random") <~ On.click {
          output <~ text(s"Changed text with random number: ${(Math.random() * 10).toInt}")
        }
      ),
      l[ScrollView](
        w[TextView] <~ wire(output)
      )
    ) <~ vertical

    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
    setContentView(getUi(view))
  }

  def search(): Future[Tweak[TextView]] = {
    setProgressBarIndeterminateVisibility(true)
    Future {
      Thread.sleep(4000)
      service.getMyIp.origin
    }.recover {
      case exc: Throwable => exc.toString
    }.mapUi { str =>
      setProgressBarIndeterminateVisibility(false)
      text(str)
    }
  }
}