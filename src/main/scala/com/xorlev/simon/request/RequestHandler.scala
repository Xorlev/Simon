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