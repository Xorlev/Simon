package com.xorlev.simon.model

import java.io.InputStream
import com.xorlev.simon.RequestParser.HeaderLine

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

object InternalServerError {
  def apply(response: InputStream, headers:List[HeaderLine] = List.empty) =
      HttpResponse(500, "text/html", response, headers)
}