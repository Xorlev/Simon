package com.xorlev.simon.util

import java.lang.String
import java.text.SimpleDateFormat
import java.util.Date
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
 * 2012-11-28
 * @author Michael Rose <michael@fullcontact.com>
 */

object HeaderUtil {
  val RFC1123_DATE_PATTERN:String = "EEE, dd MMM yyyy HH:mm:ss zzz"
  val dateFormat:SimpleDateFormat = new SimpleDateFormat(RFC1123_DATE_PATTERN)

  def formatDate(date: Date): String = {
    dateFormat.format(date)
  }

  def now(): String = {
    DateTime.now().withZone(DateTimeZone.UTC).toString(RFC1123_DATE_PATTERN)
  }
}