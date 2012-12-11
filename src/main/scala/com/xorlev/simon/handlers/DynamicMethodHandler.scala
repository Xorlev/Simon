package com.xorlev.simon.handlers

import com.xorlev.simon.RequestHandler
import com.xorlev.simon.model.{HttpRequest, HttpResponse}
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

    if (ctx.isDefinedAt((r.method, r.resource))) {
      Some(HttpResponse(
        200,
        MimeUtil.HTML,
        runRoute(request, (r.method, r.resource))
      ))
    } else {
      Some(HttpResponse(404, MimeUtil.HTML, RenderUtil.notFound()))
    }
  }

  def get(path: String)(f: =>Any) = ctx.put(("GET", path), x=>doRender(f))
  def post(path: String)(f: =>Any) = ctx.put(("POST", path), x=>doRender(f))
  def put(path: String)(f: =>Any) = ctx.put(("PUT", path), x=>doRender(f))
  def delete(path: String)(f: =>Any) = ctx.put(("DELETE", path), x=>doRender(f))
  def options(path: String)(f: =>Any) = ctx.put(("OPTIONS", path), x=>doRender(f))
  def head(path: String)(f: =>Any) = ctx.put(("HEAD", path), x=>doRender(f))

  def runRoute(request: HttpRequest, route: (String, String)): String = {
    log.debug("Running route {}", route)
    log.debug("Content-type: " + request.getContentType)
    paramsMap.withValue(mutable.Map(request.params.toSeq: _*)) {
      ctx(route)()
    }
  }
  
  def doRender(f: =>Any): String = {
    f.toString
  }
}