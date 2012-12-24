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

import java.io.{ByteArrayInputStream, InputStream}
import collection.mutable.ListBuffer
import com.xorlev.simon.util.Loggable
import com.xorlev.simon.model.{HttpResponse, HttpRequest}


abstract class RequestHandler extends Loggable {
  var filterList: ListBuffer[(String) => String] = ListBuffer()

  def handleRequest(request: HttpRequest): Option[HttpResponse]

  def filterPath(p: String): String = {
    filterList.foldLeft(p) { (pNew, op) =>
      op(pNew)
    }
  }

  protected[this] def registerFilter(f :(String) => String) {
    filterList.append(f)
  }

  implicit def stringToInputStream(s: String): InputStream = {
    new ByteArrayInputStream(s.getBytes)
  }
}