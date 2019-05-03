package reactive

import akka.actor.Actor
import akka.actor.ActorRef
import akka.event.LoggingReceive

object WireTransfer {
  case class Transfer(from: ActorRef, to: ActorRef, amount: BigInt)
  case object Done
  case object Failed
}

class WireTransfer extends Actor {
  import WireTransfer._
  
  def receive = LoggingReceive {
    case Transfer(from, to, amount) =>
      from ! Banking.Withdraw(amount)
      context.become(awaitFrom(to, amount, sender))
  }
  
  def awaitFrom(to: ActorRef, amount: BigInt, customer: ActorRef): Receive = LoggingReceive {
    case Banking.Done =>
      to ! Banking.Deposit(amount)
      context.become(awaitTo(customer))
    case Banking.Failed =>
      customer ! Failed
      context.stop(self)
  }
  
  def awaitTo(customer: ActorRef): Receive = LoggingReceive {
    case Banking.Done =>
      customer ! Done
      context.stop(self)
  }
}
