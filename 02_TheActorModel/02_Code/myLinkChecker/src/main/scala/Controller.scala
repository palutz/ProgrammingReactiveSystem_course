package it.stefano.linkChecker

import akka.actor.{ Actor, ActorRef, ActorLogging }
import akka.pattern.pipe

/* *******************
 Controls all the getters
 to properly parse the links encountered

 * ******************** */
class Controller {
  var cahe : Set[String] = Set.empty
  var  children : Set[ActorRef] = Set.empty

  def receive = {
    case Check(url, depth) => 
      log.debug("{} receive Check message {}", depth, url)

  }
}
