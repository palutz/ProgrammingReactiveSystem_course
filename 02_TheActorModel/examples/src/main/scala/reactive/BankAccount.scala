package reactive

import akka.actor.{ Actor, ActorSystem, Props }
import akka.event.LoggingReceive

object BankAccount {
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
  import BankAccount._
  // var balance = BigInt(0)

  def bankAccount(money: BigInt) : Receive = {
    case Deposit(amount) =>
      // balance += amount
      println(s"deposit $amount")
      // sender ! Done
      context.become(bankAccount(money + amount))
    case Withdraw(amount) if amount <= money =>
      // balance -= amount
      println(s"withdraw $amount")
      // sender ! Done
      context.become(bankAccount(money - amount))
    case Statement =>
      println("Bank account statement: ")
      // sender ! Done
      println(s"money= $money")
    case _ => sender ! Failed
  }

  def receive = LoggingReceive {
    println("starting receive")
    bankAccount(0)
  }
}

object TestBankActor extends App {
  println("start bank test")
  val ctx = ActorSystem("SteoAkka")
  val bA = ctx.actorOf(Props[BankAccount], "steoBank")

  bA ! BankAccount.Deposit(100)
  bA ! BankAccount.Deposit(100)
  bA ! BankAccount.Deposit(100)

  bA ! BankAccount.Withdraw(150)

  bA ! BankAccount.Statement

  ctx.terminate()
}
