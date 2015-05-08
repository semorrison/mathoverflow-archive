package org.mathoverflow.archive

import scala.slick.lifted.TableQuery
import scala.slick.driver.MySQLDriver.simple._
import java.util.Calendar
import java.util.TimeZone

object Lookup {
  def apply(post: Int, timestamp: Long): (Long, String) = {
    SQL { implicit session =>
      (for (
        r <- Tables.Archive;
        if r.post === post;
        if r.timestamp > timestamp
      ) yield r).run.headOption match {
        case Some((_, post, resultTimestamp, json)) => (resultTimestamp, json)
        case None => {
          println("Retrieving JSON from the SE API")
          val resultTimestamp = Time.now
          val json = Retrieve(post)
          Tables.Archive += (0, post, resultTimestamp, json)
          (resultTimestamp, json)
        }
      }
    }
  }
}