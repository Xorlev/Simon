package com.xorlev.simon.handlers

import com.xorlev.simon.model.{HttpRequest, HttpResponse}
import com.xorlev.simon.request.{RequestParser, RequestHandler}
import RequestParser.HeaderLine
import java.io.{FileInputStream, File}
import com.xorlev.simon.util.{HeaderUtil, RenderUtil, MimeUtil}
import com.google.common.hash.Hashing
import com.google.common.net.HttpHeaders
import java.util.regex.Pattern
import com.xorlev.simon.request.RequestHandler

/**
 * 2012-11-26
 * @author Michael Rose <elementation@gmail.com>
 */

class StaticFileRequestHandler(context: String, basePath: String) extends RequestHandler {
  val contextPattern = ("^" + context).r
  registerFilter({ contextPattern.replaceFirstIn(_, "") })
  registerFilter({ handleEmptyPath(_) })

  def handleRequest(request: HttpRequest):Option[HttpResponse] = {
    try {
      log.debug("StaticFileRequestHandler[{},{}]", basePath, request)

      val p = filterPath(request.request.resource)

      val file = openFile(p)

      file.flatMap { fi =>
        val eTag = Hashing.murmur3_128().hashLong(fi.lastModified())
        val mime = MimeUtil.processMime(fi)
        val headers = List(
          HeaderLine(HttpHeaders.ETAG, eTag.toString),
          HeaderLine(HttpHeaders.LAST_MODIFIED, HeaderUtil.formatDate(fi.lastModified()))
        )

        Some(
          HttpResponse(200, mime, new FileInputStream(fi), headers)
        )
      }.orElse {
        Some(
          HttpResponse(404, MimeUtil.HTML, RenderUtil.notFound())
        )
      }

    } catch {
      case ex:Throwable => Some(HttpResponse(500, MimeUtil.HTML, RenderUtil.renderStackTrace(ex)))
      case _ => None
    }
  }

  def handleEmptyPath(path: String): String = {
    path.split("/").drop(1).mkString("/").size match {
      case 0 => "index.html"
      case _ => path
    }
  }

  def openFile(path: String): Option[File] = {
    val absPath = if (basePath.endsWith("/")) basePath + path else basePath + "/" + path
    val f = new File(absPath)

    f.exists match {
      case true => Some(f)
      case false => None
    }
  }
}