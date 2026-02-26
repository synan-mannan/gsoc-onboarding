package gsoc
package contributors

import cats.effect.*
import fs2.concurrent.*
import fs2.dom.HtmlElement
import calico.html.io.{*, given}
import calico.syntax.*

val `the-ivii`: Contributor = Contributor("the-ivii"):
  SignallingRef[IO].of(false).toResource.flatMap { revealed =>
    div(
      p("Hello, I'm @the-ivii on GitHub. I agree to follow the Typelevel CoC and GSoC AI policy."),
      revealed.map(r =>
        if r then
          p("I am interested in working on the 'Upgrade sbt-typelevel to sbt 2' project")
        else div("")),
      button(
        styleAttr := "background-color: #0c8d10ff; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; font-weight: bold;",
        onClick --> (_.foreach(_ => revealed.update(!_))),
        revealed.map(r => if r then "Hide project info" else "Show project info")
      )
    )
  }
