package reactive

import akka.actor.{ Actor, ActorSystem, Props }
import akka.event.LoggingReceive

object Banking {
  case class Deposit(amount: BigInt) {
    require(amount > 0)
  }
  case class Withdraw(amount: BigInt) {
    require(amount > 0)
  }
  case object Statement
  case object Done
  case object Failed
}

class BankAccount extends Actor {
  import Banking._
  // var balance = BigInt(0)

  def bankAccount(money: BigInt) : Receive = {
    case Deposit(amount) =>
      // balance += amount
      println(s"deposit $amount")
      sender ! Done
      context.become(bankAccount(money + amount))
    case Withdraw(amount) if amount <= money =>
      // balance -= amount
      println(s"withdraw $amount")
      sender ! Done
      context.become(bankAccount(money - amount))
    case Statement =>
      println("Bank account statement: ")
      println(s"Money available= $money")
    case _ => sender ! Failed
  }

  // def receive = LoggingReceive {
  def receive = {
    bankAccount(0)
  }
}

// adding memory of the events
class Bank extends Actor {
  import Banking._
  val steoAccount = context.actorOf(Props[BankAccount], "steoAccount")

  // silly implementation of a queue with a list
  def bank(l : List[String]) : Receive =  {
    case Done =>
      val action = l.reverse.head
      println(s"$action Done!")
      context.become(bank(l.dropRight(1)))
    case Deposit(amount) =>
      context.become(bank("Deposit" :: l))
      steoAccount ! Deposit(amount)
    case Withdraw(amount) =>
      context.become(bank("withdraw" :: l))
      steoAccount ! Withdraw(amount)
    case Statement => steoAccount ! Statement
  }

  def receive = LoggingReceive {
    bank(List.empty)
  }
}

object TestBankActor extends App {
  import Banking._

  println("start bank test")
  val ctx = ActorSystem("SteoAkka")
  val bA = ctx.actorOf(Props[Bank], "steoBank")

  bA ! Deposit(100)
  bA ! Deposit(100)
  bA ! Deposit(100)

  bA ! Withdraw(150)

  bA ! Statement

  ctx.terminate()
}
