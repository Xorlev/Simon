package com.xorlev.simon

import com.xorlev.simon.RequestParser.HttpRequest
import java.io.{ByteArrayInputStream, InputStream, OutputStream}
import model.HttpResponse
import util.Loggable


trait RequestHandler extends Loggable {
  def handleRequest(request: HttpRequest): Option[HttpResponse]

  implicit def stringToInputStream(s: String): InputStream = {
    new ByteArrayInputStream(s.getBytes)
  }
}