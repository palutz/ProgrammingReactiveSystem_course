package a.lesson02

import akka.actor.{Actor, Props, ActorSystem }

/**
Example code like in the lesson 02.02 (with some modifications)

  */

class Counter extends Actor {
  def counter(n : Int) : Receive = {
    case "inc" => {
      println("n=" + n)
      context.become(counter(n + 1))
    }
    case "get" => sender ! n
  }
  def receive = counter(0)
}

class CounterMain extends Actor {
  val counter = context.actorOf(Props[Counter], "counter")
  def receive = {
    case n : Int => println("received " + n)
    case "add" => counter ! "inc"
    case "tot" => counter ! "get"
  }
}

object IntroToActor extends App {
  println("Starting...")
  val ctx = ActorSystem("helloAkka")
  val cm = ctx.actorOf(Props[CounterMain], "CounterMain")
  println("sending msg to main actor")
  cm ! 0
  cm ! "add"
  cm ! "add"
  cm ! "add"
  println("going to sleep....")
  Thread.sleep(1000)
  cm ! "tot"
  println("terminating...")
  ctx.terminate()
}
