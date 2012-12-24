package com.xorlev.simon.handlers

import com.xorlev.simon.RequestHandler
import com.xorlev.simon.model._
import collection.mutable.HashMap
import com.xorlev.simon.util.{RenderUtil, MimeUtil}
import collection.mutable
import util.DynamicVariable
import org.codehaus.jackson.map.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import xml.NodeSeq
import java.io.ByteArrayInputStream
import com.xorlev.simon.model.HttpRequest
import com.xorlev.simon.model.HttpResponse
import scala.Some

/**
 * 2012-12-02
 * @author Michael Rose <elementation@gmail.com>
 */

class DynamicMethodHandler extends RequestHandler {
  val ctx = new HashMap[(String, String), (Any=>HttpResponse)]
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  val paramsMap = new DynamicVariable[mutable.Map[String,String]](null)
  val requestVar = new DynamicVariable[HttpRequest](null)

  implicit def params: mutable.Map[String, String] = paramsMap.value
  implicit def request: HttpRequest = requestVar.value


  /**
   * Handles request and invokes a handler, or else it returns a 404 response
   * @param request
   * @return HttpResponse
   */
  override def handleRequest(request: HttpRequest): Option[HttpResponse] = {
    val r = request.request

    if (ctx.isDefinedAt((r.method, r.resource))) {
      Some(runRoute(request, (r.method, r.resource)))
    } else {
      Some(NotFound(RenderUtil.notFound()))
    }
  }

  /**
   * Responsible for matching the route and executing it with the proper DynamicVariables instantiated
   * @param request
   * @param route
   * @return
   */
  def runRoute(request: HttpRequest, route: (String, String)): HttpResponse = {
    log.debug("Running route {}", route)
    log.debug("Desired Content-type: " + request.getContentType)
    paramsMap.withValue(parseParams(request.params)) {
      requestVar.withValue(request) {
        try {
          ctx(route)()
        } catch {
          case ex:HaltedHandlerException => HttpResponse(ex.code, MimeUtil.HTML, RenderUtil.renderStackTrace(ex))
        }
      }
    }
  }

  private[this] def parseParams(requestParams: Map[String,String]): mutable.Map[String,String] = {
    mutable.Map(requestParams.toSeq: _*).withDefaultValue(null)
  }

  /**
   * Render pipeline, responsible for determining response format.
   * @param f is a user-defined closure to generate a response
   * @return HttpResponse(code, mime, data)
   */
  private[this] def doRender(f: =>Any): HttpResponse = {
    f match {
      case n:HttpResponse => n
      case n:NodeSeq => HttpResponse(200, MimeUtil.HTML, n.toString())
      case n:Array[Byte] => HttpResponse(200, MimeUtil.STREAM, new ByteArrayInputStream(n))
      case n:String => HttpResponse(200, MimeUtil.PLAIN, n)
      case n:Any if mapper.canSerialize(n.getClass) => HttpResponse(200, MimeUtil.JSON, mapper.writeValueAsString(n))
      case _ => UnprocessableEntity(RenderUtil.unprocessableEntity())
    }
  }

  def halt(code:Int) = throw new HaltedHandlerException(code)

  def get(path: String)(f: =>Any) = ctx.put(("GET", path), x=>doRender(f))
  def post(path: String)(f: =>Any) = ctx.put(("POST", path), x=>doRender(f))
  def put(path: String)(f: =>Any) = ctx.put(("PUT", path), x=>doRender(f))
  def delete(path: String)(f: =>Any) = ctx.put(("DELETE", path), x=>doRender(f))
  def options(path: String)(f: =>Any) = ctx.put(("OPTIONS", path), x=>doRender(f))
  def head(path: String)(f: =>Any) = ctx.put(("HEAD", path), x=>doRender(f))
}