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

package com.xorlev.simon.model

import java.io.InputStream
import com.xorlev.simon.request.RequestParser
import RequestParser.HeaderLine

/**
 * 2012-11-28
 * @author Michael Rose <elementation@gmail.com>
 */
case class HttpResponse(responseCode: Int, mimeType:String, response:InputStream, extraHeaders:List[HeaderLine] = List())

object Ok {
  def apply(response: InputStream, headers:List[HeaderLine] = List.empty) =
      HttpResponse(200, "text/html", response, headers)
}

object BadRequest {
  def apply(response: InputStream, headers:List[HeaderLine] = List.empty) =
      HttpResponse(400, "text/html", response, headers)
}

object NotFound {
  def apply(response: InputStream, headers:List[HeaderLine] = List.empty) =
      HttpResponse(404, "text/html", response, headers)
}

object UnprocessableEntity {
  def apply(response: InputStream, headers:List[HeaderLine] = List.empty) =
      HttpResponse(422, "text/html", response, headers)
}

object InternalServerError {
  def apply(response: InputStream, headers:List[HeaderLine] = List.empty) =
      HttpResponse(500, "text/html", response, headers)
}