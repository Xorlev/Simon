package com.xorlev.simon

import com.xorlev.simon.RequestParser.HttpRequest
import java.io.{ByteArrayInputStream, InputStream, OutputStream}

/**
 * 2012-11-26
 * @author Michael Rose <michael@fullcontact.com>
 */

case class HttpResponse(responseCode: Int, mimeType:String, response:InputStream)
trait RequestHandler {
  def handleRequest(request: HttpRequest): Option[HttpResponse]

  def renderStackTrace(ex: Throwable): String = {
    "<h2>" + ex.toString + "</h2>\n" +
    ex.getStackTraceString.replaceAll("\n", "<br />\n")
  }
  implicit def stringToInputStream(s: String): InputStream = {
    new ByteArrayInputStream(s.getBytes)
  }
}