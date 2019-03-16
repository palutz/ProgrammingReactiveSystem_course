package a.lesson02

import akka.actor.{Actor, Props, ActorSystem, ActorRef }

object BankAccount {
  case class Deposit(amount : BigInt) {
    require(amount > 0)
  }

  case class Withdraw(amount: BigInt) {
    require(amount > 0)
  }

  case object Done
  case object Failed
}

class BankAccount extends Actor {
  import BankAccount._

  def inner(balance: BigInt) : Receive = {
    case Deposit(a) => {
      context.become(inner(balance + a))
      sender ! Done
    }
    case Withdraw(a) if(balance >= a) => {
      context.become(inner(a))
      sender ! Done
    }
    case _ => sender ! Failed
  }

  def receive = inner(0)
}

object WireTransfer {
  case class Transfer(from : ActorRef, to: ActorRef, amount: BigInt)
  case object Done
  case object Failed
}

class WireTransfer extends Actor {
  import WireTransfer._

  def receive = {
    case Transfer(from, to, amount) =>
      from ! BankAccount.Withdraw(amount)
      context.become(awaitWithDraw(to, amount, sender))  // await for confirmation of withdraw (actor like suspends itself)

  }
}
