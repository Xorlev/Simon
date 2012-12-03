package com.xorlev.simon.handlers

import com.xorlev.simon.RequestHandler
import com.xorlev.simon.RequestParser.HttpRequest
import com.xorlev.simon.model.HttpResponse
import collection.mutable.HashMap
import com.xorlev.simon.util.{RenderUtil, MimeUtil}
import collection.mutable
import util.DynamicVariable

/**
 * 2012-12-02
 * @author Michael Rose <elementation@gmail.com>
 */

class DynamicMethodHandler extends RequestHandler {
  val ctx = new HashMap[(String, String), (Any=>String)]

  val paramsMap = new DynamicVariable[mutable.Map[String,String]](null)

  implicit def params: mutable.Map[String, String] = paramsMap.value

  override def handleRequest(request: HttpRequest): Option[HttpResponse] = {
    val r = request.request

    println(r)
    if (ctx.isDefinedAt((r.method, r.resource))) {
      val h = Some(HttpResponse(
        200,
        MimeUtil.HTML,
        runRoute(request, (r.method, r.resource))
      ))

      println(h)
      h
    } else {
      return Some(HttpResponse(404, MimeUtil.HTML, RenderUtil.notFound()))
    }
  }

  def get(path: String)(f: =>Any) = ctx.put(("GET", path), x=>f.toString)
  def post(path: String)(f: =>Any) = ctx.put(("POST", path), x=>f.toString)

  def runRoute(request: HttpRequest, route: (String, String)): String = {
    //var params = request.params
    //params += "hello" -> "world"

    log.debug("Running route {}", route)
    paramsMap.withValue(mutable.Map("hello" -> "world")) {
      ctx(route)()
    }
  }
}