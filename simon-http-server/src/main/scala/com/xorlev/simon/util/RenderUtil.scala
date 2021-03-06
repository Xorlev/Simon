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

/**
 * 2012-11-28
 * @author Michael Rose <elementation@gmail.com>
 */

object RenderUtil {
  def notFound(): String = "<h2>404: Not found</h2>"
  def badRequest(): String = "<h2>400: Bad Request</h2>"
  def unprocessableEntity(): String = "<h2>422: Unprocessable Entity</h2>"

  def renderStackTrace(ex: Throwable): String = {
    "<h2>" + ex.toString + "</h2>\n" +
      ex.getStackTraceString.replaceAll("\n", "<br />\n")
  }
}