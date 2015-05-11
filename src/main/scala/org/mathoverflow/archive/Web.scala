package org.mathoverflow.archive

import org.jboss.netty.handler.codec.http.{ HttpRequest, HttpResponse }
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{ Http, Response }
import com.twitter.finagle.Service
import com.twitter.util.Future
import java.net.InetSocketAddress
import util.Properties
import java.net.URI
import org.jboss.netty.handler.codec.http.QueryStringDecoder
import java.net.URL
import scala.io.Source
import java.io.StringWriter
import java.io.PrintWriter
import java.net.URLConnection

object Web {
  def main(args: Array[String]) {
    val port = Properties.envOrElse("PORT", "8080").toInt
    println("Starting on port:" + port)
    ServerBuilder()
      .codec(Http())
      .name("mathoverflow-archive")
      .bindTo(new InetSocketAddress(port))
      .build(new ResolverService)
    println("Started mathoverflow-archive.")

  }
}

class ResolverService extends Service[HttpRequest, HttpResponse] {

  val htmlContent = Source.fromInputStream(getClass.getResourceAsStream("/question.html")).mkString

  def apply(req: HttpRequest): Future[HttpResponse] = {
    val response = Response()
    println("received request for " + req.getUri())

    try {
      val parameters = new QueryStringDecoder(req.getUri()).getParameters
      val path = new QueryStringDecoder(req.getUri()).getPath().split("/").toSeq.tail

      if (path.head == "favicon.ico") {
        response.setStatusCode(404)
      } else if (path.head == "resources") {
        println("trying to find resource: " + path)
        val filename = path.tail.mkString("/", "/", "")
        Option(getClass.getResourceAsStream(filename)).map(is => Source.fromInputStream(is).mkString) match {
          case Some(content) => {
            response.setContentType(URLConnection.guessContentTypeFromName(filename))
            response.setStatusCode(200)
            response.contentString = content
          }
          case None => {
            response.setStatusCode(404)
          }
        }
      } else if(path.head == "question"){
        import scala.collection.JavaConverters._
        val callback = Option(parameters.get("callback")).map(_.asScala.headOption).flatten

        val jsonRequested = callback.nonEmpty || req.getUri.contains("json") || req.headers.get("Accept").contains("application/json")

        val question = path(1).toInt
        val timestamp = Time.parse(path.drop(2).mkString("/"))

        println("request timestamp: " + timestamp)

        val (resultTimestamp, json) = Lookup(question, timestamp)
        println("lookup timestamp: " + resultTimestamp)
        println(json)

        if (path.size == 4 && resultTimestamp == timestamp) {
          println("200")
          response.setStatusCode(200)

          if (jsonRequested) {
            callback match {
              case Some(c) => {
                response.setContentType("application/javascript")
                response.contentString = c + "(" + json + ");"
              }
              case None => {
                response.setContentType("application/json")
                response.contentString = json
              }
            }
          } else {
            response.setContentType("text/html")
            response.contentString = htmlContent
          }

        } else {
          response.setStatusCode(301)
          val timestampString = Time.write(resultTimestamp)
          val url = path.take(2).mkString("/", "/", "/") + timestampString
          println("redirecting to " + url)
          response.headers.set("Location", url)
        }
      } else {
        response.setStatusCode(404)
      }
      Future(response)
    } catch {
      case e: Exception => {
        response.setStatusCode(500)
        response.setContentType("text/plain")
        response.contentString = {
          val sw = new StringWriter
          e.printStackTrace(new PrintWriter(sw))
          e.printStackTrace
          sw.toString()
        }
        Future(response)
      }
    }

  }
}