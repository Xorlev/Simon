package com.xorlev.simon.util

/**
 * 2012-11-28
 * @author Michael Rose <elementation@gmail.com>
 */

object RenderUtil {
  def notFound(): String = "<h2>Not found</h2>"

  def renderStackTrace(ex: Throwable): String = {
    "<h2>" + ex.toString + "</h2>\n" +
      ex.getStackTraceString.replaceAll("\n", "<br />\n")
  }
}