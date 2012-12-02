package com.xorlev.simon

import handlers.{ErrorHandler, StaticFileRequestHandler}
import java.lang.String
import collection.JavaConversions.MutableMapWrapper

/**
 * 2012-12-02
 * @author Michael Rose <elementation@gmail.com>
 */

object RequestMapper {
  var ctx: Map[String, RequestHandler] = Map()

  def registerHandler(path: String, handler: RequestHandler) {
    synchronized {
      ctx += path -> handler
    }
  }

  def getHandler(path: String): RequestHandler = {
    ctx.find { handler =>
      path.startsWith(handler._1)
    }.flatMap { h =>
      Some(h._2)
    }.getOrElse(new ErrorHandler)
  }
}