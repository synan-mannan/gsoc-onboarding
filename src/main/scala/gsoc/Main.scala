package gsoc

import cats.data.*
import cats.effect.*
import cats.syntax.all.*

import contributors.*

import calico.IOWebApp
import calico.html.io.{*, given}
import calico.router.*
import calico.syntax.*
import calico.unsafe.given

import fs2.concurrent.*
import fs2.dom.HtmlElement

import org.http4s.dom.FetchClientBuilder
import org.http4s.syntax.all.*

val allContributors = NonEmptyList.of(
  antoniojimeneznieto,
  valencik,
  armanbilge,
  djspiewak
)

object Main extends IOWebApp:
  def render: Resource[IO, HtmlElement[IO]] =
    val handles = allContributors.map(_.handle)
    val client = FetchClientBuilder[IO].create

    Router(window).toResource.flatMap { router =>
      SignallingRef[IO].of[Option[String]](None).toResource.flatMap { validationMsg =>

        def contributorUri(handle: String) = uri"" +? ("handle" -> handle)

        def nav = ul(handles.toList.map { handle =>
          li(
            a(
              href := "#",
              onClick(router.navigate(contributorUri(handle))),
              s"@$handle"
            )
          )
        })

        val routes = allContributors.reduceMap {
          case Contributor(handle, component) =>
            Routes.one[IO] {
              case uri if uri.query.params.get("handle").contains(handle) => ()
            } { _ => component }
        }

        val validate: IO[Unit] =
          Validation.validate(handles.toList, client).flatMap(validationMsg.set)

        div(
          h1("Typelevel GSoC Onboarding"),
          div(
            styleAttr <-- validationMsg.map {
              case None =>
                "color: green; padding: 8px; border: 1px solid green; margin-bottom: 12px"
              case Some(_) =>
                "color: red; padding: 8px; border: 1px solid red; margin-bottom: 12px"
            },
            validationMsg.map(_.getOrElse("Order is correct!"))
          ),
          button(
            onClick --> (_.foreach(_ => validate)),
            "Verify order"
          ),
          nav,
          routes.toResource.flatMap(router.dispatch)
        )
      }
    }
