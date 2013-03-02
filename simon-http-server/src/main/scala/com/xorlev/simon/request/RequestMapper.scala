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

package com.xorlev.simon.request

import java.lang.String
import com.xorlev.simon.handlers.ErrorHandler
import util.matching.Regex

/**
 * 2012-12-02
 * @author Michael Rose <elementation@gmail.com>
 */

trait RequestMapper {
  def getHandler(path: String): RequestHandler
}

object StaticRequestMapper extends RequestMapper {
  var ctx: Map[String, RequestHandler] = Map()

  def registerHandler(path: String, handler: RequestHandler) {
    synchronized {
      ctx += path -> handler
    }
  }

  def getHandler(path: String): RequestHandler = {
    ctx.find { handler =>
      path.startsWith(handler._1)
    }.flatMap { h =>
      Some(h._2)
    }.getOrElse(new ErrorHandler)
  }
}
object RegexRequestMapper extends RequestMapper {
  var ctx: Map[Regex, RequestHandler] = Map()

  def registerHandler(path: String, handler: RequestHandler) {
    synchronized {
      ctx += path.replaceAll(":([a-zA-Z0-9]+)", "([\\^\\/]+)").r -> handler
    }
  }

  def getHandler(path: String): RequestHandler = {
    ctx.find { handler =>
      handler._1.pattern.matcher(path).find()
    }.flatMap { h =>
      val x = h._1.pattern.matcher(path)

      if (x.find()) {
        h._2.requestParams.set(Map(
          "helloparam" -> x.group(1)
        ))
      } else {
        h._2.requestParams.remove()
      }
      Some(h._2)
    }.getOrElse(new ErrorHandler)
  }
}