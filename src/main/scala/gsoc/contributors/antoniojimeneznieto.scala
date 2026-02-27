package gsoc
package contributors

import cats.effect.*
import cats.effect.std.Random

import fs2.concurrent.*
import fs2.dom.HtmlElement

import calico.html.io.{*, given}
import calico.syntax.*

import org.scalajs.dom
import scala.concurrent.duration.DurationInt

val antoniojimeneznieto: Contributor =
  import SnakeGame.*
  import Dir.*
  Contributor("antoniojimeneznieto"):
    for
      rng <- Random.scalaUtilRandom[IO].toResource
      food <- randPt(rng).toResource
      ref <- SignallingRef[IO].of(init(food)).toResource
      _ <- fs2
        .Stream
        .fixedRate[IO](150.millis)
        .evalMap(_ => ref.get.flatMap(g => if g.over then IO.unit else tick(ref, rng)))
        .compile
        .drain
        .background
      node <- div(
        tabIndex := 0,
        styleAttr := "outline:none;",
        p(
          "I am ",
          span(
            styleAttr := "color:mediumseagreen;font-weight:bold",
            "@antoniojimeneznieto"
          ),
          " on GitHub. I agree to follow the Typelevel CoC and GSoC AI policy."
        ),
        p(
          styleAttr := "font-size:13px;color:#888;margin:4px 0;",
          "Click here & use arrow keys to play Snake!"
        ),
        onKeyDown --> (_.foreach { e =>
          e.key match
            case "ArrowUp" => e.preventDefault *> ref.update(changeDir(_, Up))
            case "ArrowDown" => e.preventDefault *> ref.update(changeDir(_, Down))
            case "ArrowLeft" => e.preventDefault *> ref.update(changeDir(_, Left))
            case "ArrowRight" => e.preventDefault *> ref.update(changeDir(_, Right))
            case _ => IO.unit
        }),
        ref.map(renderGrid),
        ref.map { g =>
          p(
            styleAttr := "margin-top:8px;font-size:14px;",
            if g.over then s"Game Over! Score: ${g.score}" else s"Score: ${g.score}"
          )
        },
        button(
          styleAttr := "margin-top:4px;padding:4px 12px;cursor:pointer;",
          onClick --> (_.foreach(_ => randPt(rng).flatMap(fp => ref.set(init(fp))))),
          "Restart"
        )
      )
    yield node

// ---------------------------------------------------------------------------
// Snake game
// ---------------------------------------------------------------------------

private object SnakeGame {
  val Size = 15
  val CellPx = 20

  // --- Model ---------------------------------------------------------------

  final case class Pt(x: Int, y: Int)

  enum Dir:
    case Up, Down, Left, Right

  final case class State(
      snake: List[Pt],
      dir: Dir,
      food: Pt,
      score: Int,
      over: Boolean
  )

  // --- Game logic -----------------------------------------------------

  def init(food: Pt): State = State(
    snake = List(Pt(7, 7), Pt(6, 7), Pt(5, 7)),
    dir = Dir.Right,
    food = food,
    score = 0,
    over = false
  )

  def isOpposite(a: Dir, b: Dir): Boolean = (a, b) match
    case (Dir.Up, Dir.Down) | (Dir.Down, Dir.Up) => true
    case (Dir.Left, Dir.Right) | (Dir.Right, Dir.Left) => true
    case _ => false

  def actualDir(s: State): Dir =
    if s.snake.length < 2 then s.dir
    else
      val h = s.snake(0)
      val n = s.snake(1)
      if h.x > n.x then Dir.Right
      else if h.x < n.x then Dir.Left
      else if h.y < n.y then Dir.Up
      else Dir.Down

  def step(s: State): State =
    if s.over then s
    else
      val h = s.snake.head
      val nh = s.dir match
        case Dir.Up => Pt(h.x, h.y - 1)
        case Dir.Down => Pt(h.x, h.y + 1)
        case Dir.Left => Pt(h.x - 1, h.y)
        case Dir.Right => Pt(h.x + 1, h.y)
      if nh.x < 0 || nh.x >= Size || nh.y < 0 || nh.y >= Size then s.copy(over = true)
      else if s.snake.tail.contains(nh) then s.copy(over = true)
      else if nh == s.food then s.copy(snake = nh :: s.snake, score = s.score + 1)
      else s.copy(snake = nh :: s.snake.init)

  def changeDir(g: State, nd: Dir): State =
    val ad = actualDir(g)
    if isOpposite(ad, nd) || g.over then g
    else g.copy(dir = nd)

  // --- Effectful operations ------------------------------------------------

  def randPt(rng: Random[IO]): IO[Pt] =
    for
      x <- rng.betweenInt(0, Size)
      y <- rng.betweenInt(0, Size)
    yield Pt(x, y)

  def placeFood(ref: SignallingRef[IO, State], rng: Random[IO]): IO[Unit] =
    for
      pt <- randPt(rng)
      st <- ref.get
      _ <-
        if st.snake.contains(pt) then placeFood(ref, rng)
        else ref.update(_.copy(food = pt))
    yield ()

  def tick(ref: SignallingRef[IO, State], rng: Random[IO]): IO[Unit] =
    for
      ate <- ref.modify { s =>
        val ns = step(s)
        (ns, ns.score > s.score)
      }
      _ <- if ate then placeFood(ref, rng) else IO.unit
    yield ()

  // --- Rendering -----------------------------------------------------------

  def renderGrid(g: State): Resource[IO, HtmlElement[IO]] =
    div(
      styleAttr := s"display:grid;grid-template-columns:repeat($Size,${CellPx}px);gap:1px;background:#0d0d1a;padding:1px;border-radius:4px;"
    ).flatMap { gridEl =>
      Resource
        .eval(IO {
          val raw = gridEl.asInstanceOf[dom.HTMLElement]
          for
            y <- 0 until Size
            x <- 0 until Size
          do
            val pt = Pt(x, y)
            val color =
              if pt == g.snake.head then "#3cb371"
              else if g.snake.contains(pt) then "#2e8b57"
              else if pt == g.food then "#ff4444"
              else "#1a1a2e"
            val cell = dom.document.createElement("div")
            cell.setAttribute(
              "style",
              s"width:${CellPx}px;height:${CellPx}px;background:$color;border-radius:2px;"
            )
            raw.appendChild(cell)
        })
        .map(_ => gridEl)
    }

}
