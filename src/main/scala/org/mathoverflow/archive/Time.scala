package org.mathoverflow.archive

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone
import java.util.Date

object Time {
  def now = {
    val format = new SimpleDateFormat("yyyyMMdd/HHmm")
    format.setTimeZone(TimeZone.getTimeZone("UTC"))
    parse(format.format(new Date))
  }
  def parse(date: String): Long = {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    date.split("/").toSeq match {
      case Seq("") => {
        now
      }
      case Seq(date) => {
        calendar.clear()
        val format = new SimpleDateFormat("yyyyMMdd")
        calendar.setTime(format.parse(date));
        calendar.getTimeInMillis() / 1000
      }
      case Seq(date, time) => {
        calendar.clear()
        val format = new SimpleDateFormat("yyyyMMdd/HHmm")
        calendar.setTime(format.parse(date + "/" + time));
        calendar.getTimeInMillis() / 1000
      }
    }
  }
  private val format = new SimpleDateFormat("yyyyMMdd/HHmm");
  def write(date: Long): String = {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.clear();
    calendar.setTimeInMillis(date * 1000)
    format.format(calendar.getTime)
  }
  def round(date: Long): Long = parse(write(date))
}