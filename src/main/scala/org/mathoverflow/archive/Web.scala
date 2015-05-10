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
import argonaut._
import Argonaut._
import java.util.Calendar
import java.util.TimeZone
import java.text.SimpleDateFormat
import java.io.StringWriter
import java.io.PrintWriter

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

  def apply(req: HttpRequest): Future[HttpResponse] = {
    val response = Response()

    try {
      val parameters = new QueryStringDecoder(req.getUri()).getParameters
      val path = new QueryStringDecoder(req.getUri()).getPath().split("/").toSeq.tail

      import scala.collection.JavaConverters._
      val callback = Option(parameters.get("callback")).map(_.asScala.headOption).flatten

      val jsonRequested = callback.nonEmpty || req.getUri.contains("json") || req.headers.get("Accept").contains("application/json")
      
      val question = path(1).toInt
      val timestamp = Time.parse(path.drop(2).mkString("/"))

      println("received request: " + path.mkString("/"))
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
          response.setContentType("text/plain")
          response.contentString = json
        }

      } else {
        response.setStatusCode(301)
        val timestampString = Time.write(resultTimestamp)
        val url = path.take(2).mkString("/", "/", "/") + timestampString
        println("redirecting to " + url)
        response.headers.set("Location", url)
      }

      Future(response)
    } catch {
      case e: Exception => {
        response.setStatusCode(500)
        response.setContentType("text/plain")
        response.contentString = {
          val sw = new StringWriter
          e.printStackTrace(new PrintWriter(sw))
          sw.toString()
        }
        Future(response)
      }
    }

  }
}