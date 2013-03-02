/*
 * Copyright (c) 2012 Michael Rose
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
  val STREAM: String = "application/octet-stream"
  val JSON: String = "application/json"
  val PLAIN: String = "text/plain"
  val FORM: String = "application/x-www-form-urlencoded"

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