package it.stefano.linkChecker

import com.ning.http.client.AsyncHttpClient

/* *****************
 This actor will process the body of the web page

 * **************** */

trait WebClient {
  def get(url: String) : String 
}

case class BadStatus(code : Int) extends RuntimeException

object Getter extends WebClient {

  val client = new AsyncHttpClient

  def get(url: String): String = {
    val r = client.prepareGet(url).execute().get()
    var rc = r.getStatusCode
    if(rc < 400)
      r.getResponseBodyExcerpt(131072)
    else
      throw BadStatus(rc)
  }
}
