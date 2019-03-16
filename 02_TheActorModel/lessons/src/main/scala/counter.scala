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
  def receive = {
    println("receive")
    counter(0)
  }

}

class CounterMain extends Actor {
  val counter = context.actorOf(Props[Counter], "counter")
  def receive = {
    case n : Int => println("CounterMain received " + n)
    case "add" => {
      println("CounterMain add")
      counter ! "inc"
    }
    case "tot" => {
      println("CounterMain get")
      counter ! "get"
    }
  }
}

object IntroToActor extends App {
  println("Starting...")
  val ctx = ActorSystem("helloAkka")
  val cm = ctx.actorOf(Props[CounterMain], "CounterMain")
  println("sleep after creating CounterMain...")
  Thread.sleep(1000)
  println("sending msg to main actor")
  cm ! "add"
  cm ! "add"
  cm ! "add"
  println("going to sleep....")
  Thread.sleep(1000)
  println("terminating...")
  ctx.terminate()
}
