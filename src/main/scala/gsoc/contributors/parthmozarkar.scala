package gsoc
package contributors

import cats.effect.*
import fs2.concurrent.*
import fs2.dom.HtmlElement
import calico.html.io.{*, given}
import calico.syntax.*

val parthmozarkar: Contributor =
  Contributor("parthmozarkar"):
    SignallingRef[IO].of(0).toResource.flatMap { counter =>
      div(
        h2("Hi, I'm @parthmozarkar 👋"),
        p(
          "I agree to follow the Typelevel Code of Conduct and the GSoC AI policy."
        ),
        button(
          "Click me!",
          onClick --> (_.evalMap(_ => counter.update(_ + 1)).drain)
        ),
        p(
          "Button clicked: ",
          counter.map(_.toString)
        )
      )
    }