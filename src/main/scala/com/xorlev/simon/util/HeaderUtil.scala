package com.xorlev.simon.util

import java.lang.String
import java.text.SimpleDateFormat
import java.util.Date
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import com.google.common.net.MediaType

/**
 * 2012-11-28
 * @author Michael Rose <elementation@gmail.com>
 */

object HeaderUtil {
  val RFC1123_DATE_PATTERN:String = "EEE, dd MMM yyyy HH:mm:ss zzz"

  def formatDate(timestamp: Long): String = {
    new DateTime(timestamp).withZone(DateTimeZone.UTC).toString(RFC1123_DATE_PATTERN)
  }

  def now(): String = {
    DateTime.now().withZone(DateTimeZone.UTC).toString(RFC1123_DATE_PATTERN)
  }

  // None is 10
  // 1.0 => 10
  // 0.9 => 9
  // More specific is higher
  def parseAccept(accept: String): List[String] = {
    accept.split(",").map(_.trim).reverse.zipWithIndex.map { s =>
      //println(s)
      val parts = s._1.split(";")
      val scoreModifier = if (parts.size > 1) {
        val typeParts = parts(1).split("=").map(_.trim)

        (Option(typeParts(1)).map(_.toDouble).getOrElse(1.0) * 10).ceil.toInt
      } else 10

      (s._1, s._2 + scoreModifier)
    }.toList.sortWith((k1,k2) => k1._2 > k2._2).map(_._1)
  }
}