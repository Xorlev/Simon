package com.xorlev.simon.handlers

import com.xorlev.simon.model.HttpResponse
import com.xorlev.simon.RequestHandler
import com.xorlev.simon.RequestParser.HttpRequest
import java.io.{FileInputStream, FileNotFoundException, File}
import javax.activation.MimetypesFileTypeMap
import com.xorlev.simon.util.{RenderUtil, MimeUtil}

/**
 * 2012-11-26
 * @author Michael Rose <michael@fullcontact.com>
 */

class StaticFileRequestHandler(basePath: String) extends RequestHandler {
  def handleRequest(request: HttpRequest):Option[HttpResponse] = {
    try {
      log.debug("StaticFileRequestHandler[{}]", request)

      val path = handleEmptyPath(request.request.resource.split("/").drop(1).mkString("/"))
      val file = openFile(path)

      file.flatMap { fi =>
        val mime = MimeUtil.processMime(fi)
        Some(
          HttpResponse(200, mime, new FileInputStream(fi))
        )
      }.orElse {
        Some(
          HttpResponse(404, MimeUtil.processMime("html"), "<h2>Not found</h2>")
        )
      }

    } catch {
      case ex:Throwable => Some(HttpResponse(500, MimeUtil.processMime("html"), RenderUtil.renderStackTrace(ex)))
      case _ => None
    }
  }

  def handleEmptyPath(path: String): String = {
    path.size match {
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