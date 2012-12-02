package com.xorlev.simon.util

import java.lang.String
import java.io.File

/**
 * 2012-11-28
 * @author Michael Rose <elementation@gmail.com>
 */

object MimeUtil {
  val mimeTypes = Map(
    "html" -> "text/html",
    "css"  -> "text/css",
    "js"  -> "application/javascript",
    "gif"  -> "image/gif",
    "png"  -> "image/png",
    "jpg"  -> "image/jpeg",
    "jpeg"  -> "image/jpeg"
  ).withDefaultValue("text/plain")

  val HTML: String = mimeTypes("html")

  def processMime(file: File): String = {
    mimeTypes(extension(file.getAbsolutePath))
  }
  def processMime(path: String): String = {
    mimeTypes(extension(path))
  }

  private[this] def extension(filename: String): String = {
    val dot_pos = filename.lastIndexOf(".")

    if (dot_pos < 1) {
      ""
    } else {
      val file_ext = filename.substring(dot_pos + 1)

      if (file_ext.length() == 0) "" else file_ext
    }
  }
}