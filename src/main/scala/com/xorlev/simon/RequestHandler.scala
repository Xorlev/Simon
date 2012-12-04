package com.xorlev.simon

import java.io.{ByteArrayInputStream, InputStream}
import model.{HttpRequest, HttpResponse}
import util.Loggable
import collection.mutable.ListBuffer


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