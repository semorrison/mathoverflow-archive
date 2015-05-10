package org.mathoverflow.archive

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone
import java.util.Date

object Time {
  private val longFormat = new SimpleDateFormat("yyyyMMdd/HHmm")
  private val shortFormat = new SimpleDateFormat("yyyyMMdd")
  longFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
  shortFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
  private val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

  def now = {
    parse(longFormat.format(new Date))
  }
  def parse(date: String): Long = {
    try {
      calendar.clear()
      date.split("/").toSeq match {
        case Seq("") => {
          now
        }
        case Seq(date) => {
          calendar.setTime(shortFormat.parse(date));
          calendar.getTimeInMillis() / 1000
        }
        case Seq(date, time) => {
          calendar.setTime(longFormat.parse(date + "/" + time));
          calendar.getTimeInMillis() / 1000
        }
      }
    } catch {
      case e: Exception => {
        // fail as best we can
        now
      }
    }
  }
  def write(date: Long): String = {
    calendar.clear();
    calendar.setTimeInMillis(date * 1000)
    longFormat.format(calendar.getTime)
  }
  def round(date: Long): Long = parse(write(date))
}