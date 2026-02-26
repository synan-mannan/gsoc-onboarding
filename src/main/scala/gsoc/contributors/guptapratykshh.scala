package gsoc
package contributors

import cats.effect.*
import fs2.concurrent.*
import fs2.dom.HtmlElement
import calico.html.io.{*, given}
import calico.syntax.*

val guptapratykshh: Contributor = Contributor("guptapratykshh"):
  SignallingRef[IO].of(0).toResource.flatMap { count =>
    div(
      p(
        "Hello, I'm ",
        span(
          styleAttr := "color: #ff6b6b; font-weight: bold",
          "@guptapratykshh"
        ),
        " on GitHub. I agree to follow the Typelevel CoC and GSoC AI policy."
      ),
      p("i am excited to be part of Typelevel community for GSoC 2026"),
      div(
        button(
          styleAttr := "background-color: #cd704eff; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer;",
          onClick --> (_.foreach(_ => count.update(_ + 1))),
          count.map(c =>
            if c == 0 then "click me to see some magic" else s"you have clicked $c times")
        )
      ),
      count.map {
        case 0 => div("")
        case c if c % 2 == 1 =>
          div(
            p("i am excited to work on replacing JDK NIO with direct system I/O calls project")
          )
        case _ =>
          div(
            p("i will be working in the fs2 project")
          )
      }
    )
  }
