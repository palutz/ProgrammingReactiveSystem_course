package it.stefano.linkChecker


import scala.concurrent.{Future, Promise}
import akka.actor.Actor
import akka.pattern.pipe
import java.util.concurrent.Executor
import akka.actor.ActorLogging
import akka.actor.Status
import scala.concurrent.ExecutionContext
import org.jsoup.Jsoup
import scala.collection.JavaConverters._
import com.ning.http.client.AsyncHttpClient


/* *****************
 This actor will process the body of the web page
 * **************** */
class Getter(url: String, depth: Int) extends Actor with ActorLogging {
  implicit val executor = context.dispatcher.asInstanceOf[Executor with ExecutionContext]
  private val client = new MyAsyncWebClient

  // v1) *** with onComplete ***
  // val f = client get url
  // f onComplete {
  //   case Success(body) => self ! body
  //   case Failure(e) => self ! Status.Failure(e)
  // }
  // v2) *** with pipeTo ***
  // val f = client get url
  // f pipeTo self

  // v3) *** all together ***
  client get url pipeTo self


  def receive = {
    case body : String => 
      log.debug("receive message {} ", body)
      for(link <- findLinks(body))
        context.parent ! Controller.Check(link, depth)
      context.stop(self)
    case _: Status.Failure => stopIt()
  }

  def stopIt(): Unit = {
    context.parent ! Done
    context.stop(self)
  }

  def findLinks(body: String): Iterator[String] = {
    val document = Jsoup.parse(body, url)
    val links = document.select("a[href]")
    for {
      link <- links.iterator().asScala
    } yield link.absUrl("href")
  }

}
