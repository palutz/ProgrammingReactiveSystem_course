package a.lesson02

import akka.actor.{Actor, Props, ActorSystem }

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
