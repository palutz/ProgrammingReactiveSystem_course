package it.stefano.linkChecker

import akka.actor.{ Actor, ActorRef, ActorLogging }
import akka.pattern.PipeableFuture
import scala.collection.immutable.Set
import scala.concurrent.duration._



object Controller {
  case class Check(url: String, depth: Int)
  case class Result(links: Set[String])
}

/* *******************
 Controls all the getters
 to properly parse the links encountered
 * ******************** */
class Controller extends Actor with ActorLogging {
  import Controller._
  import context.dispatcher
  // context timeout is not really precise
  // context.setReceiveTimeout(10.seconds) // timeout - reset after receiving any msg
  // better to use the scheduler, a timer service, made on purpose for high volume and freq
  // just be aware of keeping the Actor approach: so no access of the actor stae from outside
  // but always sending msg to it
  context.system.scheduler.scheduleOnce(10.seconds, self, Timeout)

  var cache : Set[String] = Set.empty
  var children : Set[ActorRef] = Set.empty

  def receive = {

    case Check(url, depth) =>
      log.debug("{} checking {}", depth, url)
      if (!cache(url) && depth > 0)
        children += context.actorOf(Props(new Getter(url, depth - 1)))
      cache += url
    case Getter.Done =>
      children -= sender
      if (children.isEmpty) context.parent ! Result(cache)
    case Timeout => children foreach (_ ! Getter.Abort) // kill also all the children processes
  }
}
