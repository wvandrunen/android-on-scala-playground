package nl.q42.playground.views

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.{Gravity, LayoutInflater, Window}
import android.widget._
import com.example.macroid.starter.{R, TR, TypedActivity}
import nl.q42.playground.services.WhatIsMyIpServiceFactory
import nl.q42.playground.utils.TransformerExtension

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// import macroid stuff
import macroid.FullDsl._
import macroid._
import macroid.contrib._

import TransformerExtension._

// define our helpers in a mixable trait
trait Styles {
  // sets text, large font size and a long click handler
  def caption(cap: String)(implicit ctx: AppContext): Tweak[TextView] =
    text(cap) + TextTweaks.large + On.longClick {
      (toast("I’m a caption") <~ gravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL) <~ fry) ~
      Ui(true)
    }
}

class MainActivity extends Activity with Contexts[Activity] with TypedActivity with AutoLogTag with IdGeneration {

  lazy val service = WhatIsMyIpServiceFactory.get()
  lazy val inflater = getApplicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

  lazy val button = findView(TR.button)
  lazy val buttonTwo = findView(TR.button_two)

  var output = slot[TextView]

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)

    val xmlView = inflater.inflate(R.layout.test_layout, null)

    val view = l[LinearLayout](
      l[ScrollView](
        w[TextView] <~ id(Id.output) <~ wire(output)
      ),
      l[LinearLayout](
        w[Button] <~ text("Get My IP (async)") <~ On.click {
          output <~ getMyIpAddress
        },
        w[Button] <~ text("Change text with random") <~ On.click {
          output <~ text(s"Changed text with random number (sync): ${(Math.random() * 10).toInt}")
        }
      ),
      Ui(xmlView)
    ) <~ vertical

    val viewWithButtonAction = view.onClick(R.id.button) {
      val output = button.getRootView.findViewById(Id.output).asInstanceOf[TextView]
      output <~ text("First button from layout XML clicked")
    }

    val endState = viewWithButtonAction <~ Transformer {
      case button: Button if button.getId == TR.button_two.id => button <~ On.click {
        val output = button.getRootView.findViewById(Id.output).asInstanceOf[TextView]
        output <~ text("Second button from layout XML clicked")
      }
    }

    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
    setContentView(getUi(endState))
  }

  def getMyIpAddress: Future[Tweak[TextView]] = {
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