package reactive

import akka.actor.{Actor, ActorSystem, Props }

class Counter extends Actor {
  def counter(n: Int) : Receive = {
    case "incr" ⇒ context.become(counter(n + 1))
    case "get"  ⇒ println(s"total=$n")
  }

  def receive =
    counter(0)

}

object TestCounter extends App {
  println("test counter")
  val ctx = ActorSystem("SteoAkka")
  val cc = ctx.actorOf(Props[Counter], "aCounter")

  cc ! "incr"
  cc ! "incr"
  cc ! "incr"
  cc ! "get"

  ctx.terminate()
}
