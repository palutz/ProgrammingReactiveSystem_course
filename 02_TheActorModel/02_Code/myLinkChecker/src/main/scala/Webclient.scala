package it.stefano.linkChecker

import scala.concurrent.{Future, Promise }
import java.util.concurrent.Executor
import com.ning.http.client.AsyncHttpClient

case class BadStatus(code : Int) extends RuntimeException

trait WebClient {
  def get(url: String)(implicit exec: Executor): Future[String]
}


object MyAsyncWebClient extends WebClient {
  private val client = new AsyncHttpClient

  def get(url: String)(implicit ex : Executor): Future[String] = {
    val f = client.prepareGet(url).execute()
    val p = Promise[String]()
    f.addListener(
      new Runnable {
        def run = {
          val r = f.get
          val rc = r.getStatusCode
          if(rc < 400)
            p.success(r.getResponseBodyExcerpt(131072))
          else
            p.failure(BadStatus(rc))
        }
      }, ex)
    p.future
  }

  def shutdown(): Unit = client.close()
}


object MyWebClientTest extends App {
  import scala.concurrent.ExecutionContext.Implicits.global

  MyAsyncWebClient get "http://www.google.com/" map println andThen {
    case _ => MyAsyncWebClient.shutdown()
  }
}
