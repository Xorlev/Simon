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

import com.xorlev.simon.model._
import collection.mutable.ListBuffer
import collection.mutable
import util.DynamicVariable
import xml.NodeSeq
import java.io.ByteArrayInputStream
import scala.Some
import com.xorlev.simon.request.{SinatraPathPatternParser, PathPattern, RequestHandler}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.xorlev.simon.util.{RenderUtil, MimeUtil}
import org.fusesource.scalate._

/**
 * Dynamic App handler
 * Implements a Sinatra-like API to develop applications
 * 2012-12-02
 * @author Michael Rose <elementation@gmail.com>
 */

object SinatraHandlerShared {
  val mapper = new ObjectMapper()
  val engine = new TemplateEngine()
  mapper.registerModule(DefaultScalaModule)
}

class SinatraHandler extends RequestHandler {
  var routes = Vector[(String, PathPattern, HttpRequest => HttpResponse)]()


  val paramsMap = new DynamicVariable[Map[String,_]](null)
  val requestVar = new DynamicVariable[HttpRequest](null)

  implicit def params: Map[String, _] = paramsMap.value
  implicit def request: HttpRequest = requestVar.value


  /**
   * Handles request and invokes a handler, or else it returns a 404 response
   * @param request
   * @return HttpResponse
   */
  override def handleRequest(request: HttpRequest): Option[HttpResponse] = {
    val r = request.request

//    if (ctx.isDefinedAt((r.method, r.resource))) {
//      //Some(runRoute(request, (r.method, r.resource)))
//    } else {
//      Some(NotFound(RenderUtil.notFound()))
//    }

    log.debug("Finding routes for {}", r)
    var response:Option[HttpResponse] = None
    routes.foreach { x =>
      //println(x._1 + x._2.toString)
      //println(x._2.apply(r.resource))
      //println(x._2.apply(r.resource).isDefined && x._1 == r.method)
      if (x._2.apply(r.resource).isDefined && x._1 == r.method) {
        try {
          response = Some(runRoute(request, extractParams(x._2.apply(r.resource).getOrElse(Map.empty)), x._3))
        } catch {
          case e:Throwable => {
            log.error("Error processing callback", e)
            response = Some(HttpResponse(500, MimeUtil.HTML, RenderUtil.renderStackTrace(e)))
          }
        }
      }
    }

    response

//    routes.foreach {
//
//    }
//    routes.collectFirst {
//      case (method, regex, callback) if method == r.method && regex.apply(r.resource).isDefined => {
//          log.debug("Running route {}", r.resource)
//          Some(runRoute(request, extractParams(regex.apply(r.resource).getOrElse(Map.empty)), callback))
//        }
//      case _ => {
//        log.error("WTF")
//        None
//      }
//    }.getOrElse(Some(NotFound(RenderUtil.notFound())))
  }

  /**
   * Responsible for executing a callback with the proper DynamicVariables instantiated
   * @param request
   * @param routeParams
   * @param callback
   * @return
   */
  def runRoute(request: HttpRequest, routeParams: Map[String,String], callback: HttpRequest => HttpResponse): HttpResponse = {
    log.debug("Desired Content-type: " + request.getContentType)
    val params = routeParams ++ request.params
    paramsMap.withValue(params) {
      requestVar.withValue(request) {
        try {
          callback(request)
        } catch {
          case ex:HaltedHandlerException => HttpResponse(ex.code, MimeUtil.HTML, ex.haltMessage)
        }
      }
    }
  }

  private[this] def extractParams(params: Map[_, _]): Map[String,String] = {
    params.map { xs =>
      xs._1.toString -> xs._2.asInstanceOf[ListBuffer[_]].head.toString
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
      case n:Any if SinatraHandlerShared.mapper.canSerialize(n.getClass) => HttpResponse(200, MimeUtil.JSON, SinatraHandlerShared.mapper.writeValueAsString(n))
      case _ => UnprocessableEntity(RenderUtil.unprocessableEntity())
    }
  }

  def halt(code:Int) = throw new HaltedHandlerException(code)
  def halt(code:Int, msg:String) = throw new HaltedHandlerException(code, msg)

  def get(path: String)(f: =>Any) = addHandler("GET", path, f)
  def post(path: String)(f: =>Any) = addHandler("POST", path, f)
  def put(path: String)(f: =>Any) = addHandler("PUT", path, f)
  def delete(path: String)(f: =>Any) = addHandler("DELETE", path, f)
  def options(path: String)(f: =>Any) = addHandler("OPTIONS", path, f)
  def head(path: String)(f: =>Any) = addHandler("HEAD", path, f)

  def view(path: String) = {
    SinatraHandlerShared.engine.layout(path, Map(
      "request" -> request,
      "params" -> params
    ))
  }

  private[this] def addHandler(method: String, path: String, f: =>Any) {
    //ctx.put((method, path), x=>doRender(f))
    addRoute(method, path)(x=>doRender(f))
  }

  def addRoute(method: String, path: String)(callback: HttpRequest => HttpResponse) {
    val regex = SinatraPathPatternParser(path)

    routes = routes :+ (method, regex, callback)
  }
}