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

import com.xorlev.simon.request.RequestParser
import RequestParser.{HeaderLine, RequestLine}
import com.xorlev.simon.util.HeaderUtil

/**
 * 2012-12-03
 * @author Michael Rose <michael@fullcontact.com>
 */

case class HttpRequest(request: RequestLine, headers: Map[String, String], params: Map[String,String] = Map(), body: String) {
  def getContentType: String = {
    HeaderUtil.parseAccept(headers.get("Accept").get)(0)
  }
  def getContentLength: Int = {
    headers.get("Content-Length") match {
      case Some(x) => Integer.parseInt(x)
      case None => 0
    }
  }
}