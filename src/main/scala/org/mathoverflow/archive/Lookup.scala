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
        if r.timestamp >= timestamp
      ) yield r).run.headOption match {
        case Some((_, post, resultTimestamp, _, _, formattedJSON)) => (resultTimestamp, formattedJSON)
        case None => {
          println("Retrieving JSON from the SE API")
          val resultTimestamp = Time.now
          val (requestURL, json) = Retrieve(post)
          val formattedJSON = {
            import argonaut._, Argonaut._
            Parse.parseOption(json).get.spaces2
          }
          Tables.Archive += (0, post, resultTimestamp, requestURL, json, formattedJSON)
          (resultTimestamp, formattedJSON)
        }
      }
    }
  }
}