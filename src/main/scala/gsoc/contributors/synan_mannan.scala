package gsoc
package contributors

import cats.effect.*
import cats.effect.std.Random
import cats.syntax.all.*
import fs2.concurrent.*
import calico.html.io.*
import calico.html.io.given
import calico.syntax.*

val synan_mannan: Contributor =
  Contributor("synan-mannan")(
    Resource.eval(SignallingRef[IO].of(false)).flatMap { showGame =>
      Resource.eval(SignallingRef[IO].of("Click flip to start!")).flatMap { result =>
        Resource.eval(SignallingRef[IO].of(0)).flatMap { headsScore =>
          Resource.eval(SignallingRef[IO].of(0)).flatMap { tailsScore =>
            Resource.eval(Random.scalaUtilRandom[IO]).flatMap { random =>
              div(
                styleAttr :=
                  """
                  padding: 30px;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  background:  #000000;
                  font-family: Arial, sans-serif;
                  """,
                showGame.map { playing =>
                  if (!playing) {

                    div(
                      styleAttr :=
                        """
                        background: white;
                        padding: 40px;
                        border-radius: 16px;
                        width: 420px;
                        text-align: center;
                        box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                        """,
                      h2(
                        "Hi, I'm ",
                        span(
                          styleAttr := "color: #ff6b6b; font-weight: bold;",
                          "@synan-mannan"
                        ),
                        " on GitHub"
                      ),
                      p(
                        styleAttr := "margin-top: 12px; color: #444;",
                        "I enjoy contributing, exploring functional programming, ",
                        "and helping others understand Cats Effect and the Typelevel ecosystem."
                      ),
                      p(
                        styleAttr := "font-size: 14px; color: #777;",
                        "I agree to follow the \"Typelevel code of conduct\" and \"Typelevel GSoC AI policy.\""
                      ),
                      button(
                        "Let's Play Flip Coin",
                        styleAttr :=
                          """
                          margin-top: 20px;
                          padding: 12px 20px;
                          border: none;
                          border-radius: 8px;
                          background: #000000;
                          color: white;
                          font-weight: bold;
                          cursor: pointer;
                          font-size: 15px;
                          """,
                        onClick --> (_.foreach { _ =>
                          for {
                            _ <- headsScore.set(0)
                            _ <- tailsScore.set(0)
                            _ <- result.set("Click flip to start!")
                            _ <- showGame.set(true)
                          } yield ()
                        })
                      )
                    )

                  } else {

                    div(
                      styleAttr :=
                        """
                        background: white;
                        padding: 40px;
                        border-radius: 16px;
                        width: 420px;
                        text-align: center;
                        box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                        """,
                      h2("Flip a Coin"),
                      button(
                        "Flip",
                        styleAttr :=
                          """
                          margin-top: 20px;
                          padding: 12px 24px;
                          border: none;
                          border-radius: 8px;
                          background: #ff6b6b;
                          color: white;
                          font-weight: bold;
                          cursor: pointer;
                          font-size: 16px;
                          """,
                        onClick --> (_.foreach { _ =>
                          for {
                            coin <- random.nextBoolean
                            _ <-
                              if (coin) {
                                for {
                                  _ <- headsScore.update(_ + 1)
                                  _ <- result.set(
                                    "yoho it's Heads! and i will contribute to Laika!")
                                } yield ()
                              } else {
                                for {
                                  _ <- tailsScore.update(_ + 1)
                                  _ <- result.set("Boom i got Tails! Time to enhance Laika!")
                                } yield ()
                              }
                          } yield ()
                        })
                      ),
                      div(
                        styleAttr :=
                          """
                          margin-top: 20px;
                          padding: 12px;
                          border-radius: 8px;
                          background: #f3f4f6;
                          font-weight: bold;
                          """,
                        result.map(r => span(r))
                      ),
                      div(
                        styleAttr :=
                          """
                          margin-top: 20px;
                          display: flex;
                          justify-content: space-around;
                          font-weight: bold;
                          """,
                        headsScore.map(h =>
                          span(
                            styleAttr :=
                              "background:#e6fffa; padding:8px 12px; border-radius:8px;",
                            s"Heads: $h"
                          )),
                        tailsScore.map(t =>
                          span(
                            styleAttr :=
                              "background:#fff5f5; padding:8px 12px; border-radius:8px;",
                            s"Tails: $t"
                          ))
                      ),
                      button(
                        "⬅ Back",
                        styleAttr :=
                          """
                          margin-top: 24px;
                          padding: 10px 18px;
                          border: none;
                          border-radius: 8px;
                          background: #000000;
                          color: white;
                          cursor: pointer;
                          """,
                        onClick --> (_.foreach { _ => showGame.set(false) })
                      )
                    )
                  }
                }
              )
            }
          }
        }
      }
    }
  )
