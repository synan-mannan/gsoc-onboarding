# Typelevel GSoC 2026 Onboarding

Welcome! This project is part of the onboarding process for [Google Summer of Code 2026](https://summerofcode.withgoogle.com/) applicants interested in [Typelevel](https://typelevel.org/).

The app is built with [Calico](https://www.armanbilge.com/calico/), a purely functional, reactive UI framework for Scala.js powered by [Cats Effect](https://typelevel.org/cats-effect/) and [FS2](https://fs2.io/).

## What you need to do

1. **Create your own component** in `src/main/scala/gsoc/contributors/` as a new file named after your GitHub handle (e.g., `yourgithubhandle.scala`).

2. **Be creative!** Look at the existing contributors for inspiration — some have made interactive buttons, others reveal hidden info. But you are not limited to buttons. Build whatever you want: a mini game, an animation, a quiz, some generative art... surprise us!

3. **Register your component** by adding it to the `allContributors` list in [all.scala](src/main/scala/gsoc/contributors/all.scala).

> **The order matters!** The list of contributors must follow a specific ordering that is validated by a backend API. You won't know the correct position in advance — use the "Check order" button in the app to test your placement. If the order is wrong, the API will tell you which two entries are out of place. Keep trying different positions until the validation passes!

## Creating your component

Your file should follow this structure:

```scala
package gsoc
package contributors

import cats.effect.*
import fs2.concurrent.*
import fs2.dom.HtmlElement
import calico.html.io.{*, given}
import calico.syntax.*

val yourgithubhandle: Contributor = Contributor("yourgithubhandle"):
  // Build your component here!
  // It must return a Resource[IO, HtmlElement[IO]]
  div(
    p("Hello, I'm @yourgithubhandle on GitHub. I agree to follow the Typelevel CoC and GSoC AI policy."),
    // ... get creative!
  )
```

A `Contributor` is simply a GitHub handle paired with a Calico component:

```scala
case class Contributor(handle: String, component: Resource[IO, HtmlElement[IO]])
```

For stateful, interactive components you can use `SignallingRef`:

```scala
val yourgithubhandle: Contributor = Contributor("yourgithubhandle"):
  SignallingRef[IO].of(initialState).toResource.flatMap { state =>
    div(
      // Use `state` to build reactive UI
    )
  }
```

## Running locally

Make sure you have [sbt](https://www.scala-sbt.org/) installed, then:

```sh
# Compile Scala to JavaScript
sbt ~fastLinkJS

# In another terminal, start the local server
sbt >> serve
```

Open the URL printed by the `serve` task in your browser.

## Useful resources

- [Calico documentation](https://www.armanbilge.com/calico/)
- [Cats Effect documentation](https://typelevel.org/cats-effect/)
- [Scala.js documentation](https://www.scala-js.org/doc/)
- [Typelevel Code of Conduct](https://typelevel.org/code-of-conduct)

Good luck, and have fun!
