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

// making all the code more reactive
// this actor will just send the msgs to the counter
// and it will receive the answer in the receive msg (an msg with an int, the tot)
class CounterMain2 extends Actor {
  val counter = context.actorOf(Props[Counter], "counter")

  counter ! "inc"
  counter ! "inc"
  counter ! "inc"

  counter ! "get"


  // print out what will be received as a msg 
  def receive = {
    case count : Int =>
      println(s"the result is $count")
  }

}

object IntroToActor extends App {
  println("Starting...")
  val ctx = ActorSystem("helloAkka")
  val cm = ctx.actorOf(Props[CounterMain2], "CounterMain")
  // println("sleep after creating CounterMain...")
  // Thread.sleep(1000)
  // println("sending msg to main actor")
  // cm ! "add"
  // cm ! "add"
  // cm ! "add"
  // cm ! "tot"
  // println("going to sleep....")
  // Thread.sleep(1000)
  // println("terminating...")
  ctx.terminate()
}

/* Output in the first configuration (CounterMain)
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

/* Output in the second configuration (CounterMain2)
 Starting...
 receive
 n=0
 n=1
 n=2
 the result is 3
 */
