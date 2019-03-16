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
  def inner : Receive = {
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
  def receive = {
    println("CounterMain receive")
    inner
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
  cm ! "tot"
  println("going to sleep....")
  Thread.sleep(1000)
  println("terminating...")
  ctx.terminate()
}

/* Output in this configuration:
 Starting...
 sleep after creating CounterMain...
 CounterMain receive
 receive
 sending msg to main actor
 going to sleep....
 CounterMain add
 CounterMain add
 CounterMain add
 n=0
 CounterMain get
 n=1
 n=2
 CounterMain received 3
 terminating...
 */
