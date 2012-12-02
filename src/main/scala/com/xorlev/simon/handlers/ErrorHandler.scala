package com.xorlev.simon.handlers

import com.xorlev.simon.RequestHandler
import com.xorlev.simon.RequestParser.HttpRequest
import com.xorlev.simon.model.HttpResponse
import com.xorlev.simon.util.{RenderUtil, MimeUtil}

/**
 * 2012-11-29
 * @author Michael Rose <elementation@gmail.com>
 */

class ErrorHandler extends RequestHandler {
  def handleRequest(request: HttpRequest): Option[HttpResponse] = {
    Some(HttpResponse(404, MimeUtil.HTML, RenderUtil.notFound()))
  }
}