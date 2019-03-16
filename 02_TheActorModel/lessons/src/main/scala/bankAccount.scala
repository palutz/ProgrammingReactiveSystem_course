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
      context.become(awaitWithdraw(to, amount, sender))  // await for confirmation of withdraw (actor like suspends itself)
  }

  def awaitWithdraw(to: ActorRef, amount: BigInt, client: ActorRef) : Receive = {
    case BankAccount.Done =>
      to ! BankAccount.Deposit(amount)
      context.become(awaitDeposit(client))
    case BankAccount.Failed =>
      client ! Failed
      context.stop(self)
  }

  def awaitDeposit(client: ActorRef) : Receive = {
    case BankAccount.Done =>
      client ! Done  // confirm the success to the original client
      context.stop(self)
  }
}

class TransferMain extends Actor {
  val accA = context.actorOf(Props[BankAccount], "accountA")
  val accB = context.actorOf(Props[BankAccount], "accountB")

  accA ! BankAccount.Deposit(100)

  def receive = {
    case BankAccount.Done => transfer(50)
  }

  def transfer(a : BigInt) : Unit = {
    val t = context.actorOf(Props[WireTransfer], "transfer")
    t ! WireTransfer.Transfer(accA, accB, a)
    context.become {
      case WireTransfer.Done =>
        println("Done")
        context.stop(self)
    }
  }
}

object TransferActorMain extends App {
  val ctx = ActorSystem("helloAkka")
  val bb = ctx.actorOf(Props[TransferMain], "trMain")

  ctx.terminate()
}
