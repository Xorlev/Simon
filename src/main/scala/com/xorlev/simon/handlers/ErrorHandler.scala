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

package com.xorlev.simon.handlers

import com.xorlev.simon.model.{HttpRequest, HttpResponse}
import com.xorlev.simon.util.{RenderUtil, MimeUtil}
import com.xorlev.simon.request.RequestHandler

/**
 * 2012-11-29
 * @author Michael Rose <elementation@gmail.com>
 */

class ErrorHandler extends RequestHandler {
  def handleRequest(request: HttpRequest): Option[HttpResponse] = {
    Some(HttpResponse(404, MimeUtil.HTML, RenderUtil.notFound()))
  }
}