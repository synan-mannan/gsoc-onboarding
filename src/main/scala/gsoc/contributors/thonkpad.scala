package gsoc
package contributors

import cats.effect.*

import fs2.concurrent.*
import fs2.dom.HtmlElement

import calico.html.io.{*, given}
import calico.syntax.*

def spoiler(text: String): Resource[IO, HtmlElement[IO]] =
  SignallingRef[IO].of(false).toResource.flatMap { isHovered =>
    span(
      styleAttr <-- isHovered.map(h =>
        if h then "background-color: gray; cursor: pointer;"
        else "background-color: gray; color: transparent; user-select: none; cursor: pointer;"),
      onMouseOver --> (_.foreach(_ => isHovered.set(true))),
      onMouseOut --> (_.foreach(_ => isHovered.set(false))),
      text
    )
  }

val endofunctors: String = "endofunctors!"

val thonkpad: Contributor = Contributor("thonkpad"):
  SignallingRef[IO].of(false).toResource.flatMap { revealed =>
    div(
      p(
        "I am ",
        revealed.map(r => if r then "Scala" else "inherit").changes.map { color =>
          span(
            styleAttr := s"color: $color; font-weight: bold",
            "@thonkpad"
          )
        },
        " on GitHub. I agree to follow the Typelevel CoC and GSoC AI policy."
      ),
      revealed.map(r =>
        if r then
          div(
            p(s"A monad is a monoid in the category of ", spoiler(endofunctors))
          )
        else div(s"")),
      button(
        onClick --> (_.foreach(_ => revealed.update(!_))),
        revealed.map(r =>
          if r then "Hide this"
          else "Click for a pop quiz!")
      )
    )
  }
