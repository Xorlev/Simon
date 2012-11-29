package com.xorlev.simon.util

/**
 * 2012-11-28
 * @author Michael Rose <michael@fullcontact.com>
 */

object RenderUtil {
  def renderStackTrace(ex: Throwable): String = {
    "<h2>" + ex.toString + "</h2>\n" +
      ex.getStackTraceString.replaceAll("\n", "<br />\n")
  }
}