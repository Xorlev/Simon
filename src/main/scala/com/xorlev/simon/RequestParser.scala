package com.xorlev.simon

import scala.io.Source
import util.Loggable
import java.io.{InputStreamReader, InputStream}
import com.google.common.io.{CharStreams, LineReader}

/**
 * 2012-11-25
 * @author Michael Rose <elementation@gmail.com>
 */

object RequestParser extends Loggable {
  case class RequestLine(method: String, resource: String, version: String)
  case class HeaderLine(name: String, value: String)
  case class HttpRequest(request: RequestLine, headers: List[HeaderLine],
                         body: String)

  def parseHeaders(headers: Iterator[String]): Option[List[HeaderLine]] = {
    try {
      Some(headers.map { line =>
        val Array(name, value) = line.split(":", 2).map(_.trim)
        HeaderLine(name, value)
      }.toList)
    } catch {
      case ex: Throwable => None
    }
  }

  def getBody(lines: Iterator[String]): Option[String] = {
    Some("")
    //Some(lines.foldLeft("")(_+_))
  }

  def decodeRequest(stream: InputStream): Option[HttpRequest] = {
    log.debug("Decoding...")
    val lines = Source.fromInputStream(stream).getLines()

    try {
      for (
        req <- parseRequestLine(lines.next());
        headers <- parseHeaders(lines.takeWhile({ s => s.contains(':') }));
        body <- getBody(lines)
        ) yield HttpRequest(req, headers, body)
    } catch {
      case ex:Throwable => None
    }
  }

  def parseRequestLine(line: String): Option[RequestLine] = {
    if (List("GET", "POST", "PUT", "DELETE").contains(line.split(' ')(0))) {
      val Array(method, resource, version) = line.split(' ')

      return Some(RequestLine(method, resource, version))
    }

    None
  }
}