package org.mathoverflow.archive

import scala.io.Source
import scala.slick.driver.MySQLDriver.simple._
import scala.collection.JavaConverters._


object SQL {
  def apply[A](closure: slick.driver.MySQLDriver.backend.Session => A): A = Database.forURL("jdbc:mysql://mysql.tqft.net/mathoverflowarchive?user=mathoverflow&password=ambulapcha", driver = "com.mysql.jdbc.Driver") withSession closure
}

class archive(tag: Tag) extends Table[(Int, Int, Long, String)](tag, "archive") {
  def id = column[Int]("id", O.PrimaryKey)
  def post = column[Int]("post")
  def timestamp = column[Long]("timestamp")
  def json = column[String]("json")
  def * = (id, post, timestamp, json)
}

object Tables {
  val Archive = TableQuery[archive]
}