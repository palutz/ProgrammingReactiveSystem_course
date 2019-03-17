package a.lesson02

import akka.actor.{Actor, Props, ActorSystem, ActorRef }
import akka.event.LoggingReceive

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

  def inner(balance: BigInt) : Receive = LoggingReceive {
    case Deposit(a) =>
      sender ! Done
      context.become(inner(balance + a))
    case Withdraw(a) if(balance >= a) =>
      sender ! Done
      context.become(inner(a))
    case _ =>
      sender ! Failed
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
      context.become(awaitWithdraw(to, amount, sender))  // await for confirmation of withdraw (actor like suspends itself)
  }

  def awaitWithdraw(to: ActorRef, amount: BigInt, client: ActorRef) : Receive = LoggingReceive {
    case BankAccount.Done =>
      to ! BankAccount.Deposit(amount)
      context.become(awaitDeposit(client))
    case BankAccount.Failed =>
      client ! Failed
      context.stop(self)
  }

  def awaitDeposit(client: ActorRef) : Receive =  LoggingReceive {
    case BankAccount.Done =>
      client ! Done  // confirm the success to the original client
      context.stop(self)
  }
}

class TransferMain extends Actor {
  val accA = context.actorOf(Props[BankAccount], "accountA")
  val accB = context.actorOf(Props[BankAccount], "accountB")

  accA ! BankAccount.Deposit(100)

  def receive = LoggingReceive {
    case BankAccount.Done =>
      println("deposit done.. tranferring")
      transfer(50)
    case BankAccount.Failed =>
      println("BA failed")
  }

  def transfer(a : BigInt) : Unit = {
    val t = context.actorOf(Props[WireTransfer], "transfer")
    t ! WireTransfer.Transfer(accA, accB, a)
    context.become(LoggingReceive{
      case WireTransfer.Done =>
        println("Success transfer")
        context.stop(self)
      case WireTransfer.Failed =>
        println("Failed")
        context.stop(self)
    })
  }
}

object TransferActorMain extends App {
  val ctx = ActorSystem("helloAkka")
  val bb = ctx.actorOf(Props[TransferMain], "trMain")

  ctx.terminate()
}
