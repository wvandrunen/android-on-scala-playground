package nl.q42.playground.utils

import android.widget.{Button, LinearLayout}
import macroid.FullDsl._
import macroid.{AppContext, Transformer, Ui}
import scala.language.implicitConversions

object TransformerExtension {

  class RichUiView(ui: Ui[LinearLayout]) {
    def onClick(id: Int)(codeBlock: => Ui[Any])(implicit ctx: AppContext) = {
      ui <~ Transformer {
        case button: Button if button.getId == id => button <~ On.click {
          codeBlock
        }
      }
    }
  }

  implicit def richUiView(ui: Ui[LinearLayout]): RichUiView = new RichUiView(ui)
}

