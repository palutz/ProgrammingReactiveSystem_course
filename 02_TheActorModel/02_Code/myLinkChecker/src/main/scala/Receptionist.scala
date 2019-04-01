package it.stefano.linkChecker

import akka.actor.{Actor, ActorRef, ActorLogging, Props }

/****************************
 Manage one contoller for
 one request
 * ************************/
class Receptionist extends Actor {
  def receive = waiting
  val waiting: Receive = {
    // upon Get(url) start a traversal and become running
  }
  def running(queue: Vector[Job]): Receive = {
    // upon Get(url) apppend that to queue and keep running
    // upon Controller.Result(links) ship that to client
    // and run next job from queue (if any)
  }

}


 // create an actor to simulate a queue
case class Job(client: ActorRef, url: String) extends Actor {
  var reqNo = 0

  def runNext(queue: Vector[Job]): Receive = {
    reqNo += 1
    if (queue.isEmpty) waiting
    else {
      val controller = context.actorOf(Props[Controller], s"c$reqNo") //reqNo - unique number for every request 
      controller ! Controller.Check(queue.head.url, 2)
      running(queue)
    }
  }
  def enqueueJob(queue: Vector[Job], job: Job): Receive = {
    if (queue.size > 3) {
      sender ! Failed(job.url)
      running(queue)
    } else running(queue :+ job)
  }

  val waiting: Receive = {
    case Get(url) => context.become(runNext(Vector(Job(sender, url))))
  }
  def running(queue: Vector[Job]): Receive = {
    case Controller.Result(links) =>
      val job = queue.head
      job.client ! Result(job.url, links)
      context.stop(sender)
      context.become(runNext(queue.tail))
    case Get(url) =>
      context.become(enqueueJob(queue, Job(sender, url)))
  }
}
