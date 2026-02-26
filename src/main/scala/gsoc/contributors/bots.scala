package gsoc
package contributors

import cats.effect.*
import cats.syntax.all.*

import fs2.concurrent.*
import fs2.dom.HtmlElement

import calico.html.io.{*, given}
import calico.syntax.*

val `typelevel-bot` = Contributor("typelevel-bot", mkBio("typelevel-bot"))
val `scala-steward` = Contributor("scala-steward", mkBio("scala-steward"))
val `octocat` = Contributor("octocat", mkBio("octocat"))

def mkBio(handle: String) = SignallingRef[IO].of(false).toResource.flatMap { beepBoop =>
  div(
    p(
      "I am ",
      strong(s"@$handle"),
      " on GitHub. I am a bot account. ",
      (beepBoop: Signal[IO, Boolean]).ifF("Beep!", "Boop!")
    ),
    button(
      onClick --> (_.foreach(_ => beepBoop.update(!_))),
      (beepBoop: Signal[IO, Boolean]).ifF("Boop!", "Beep!")
    )
  )
}
