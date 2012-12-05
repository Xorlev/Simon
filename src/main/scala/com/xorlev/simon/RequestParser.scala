package com.xorlev.simon

import model.HttpRequest
import scala.io.Source
import util.Loggable
import java.io.InputStream

/**
 * 2012-11-25
 * @author Michael Rose <elementation@gmail.com>
 */

object RequestParser extends Loggable {
  case class RequestLine(method: String, resource: String, version: String)
  case class HeaderLine(name: String, value: String)
  val requestPattern = "(GET|POST|PUT|DELETE|OPTIONS|HEAD) (.*) (HTTP/1.0|HTTP/1.1)".r
  val paramPattern = "[?&]([^=]+)=([^=&]+)".r

  def parseHeaders(headers: Iterator[String]): Option[List[(String, String)]] = {
    try {
      Some(headers.map { line =>
        val Array(name, value) = line.split(":", 2).map(_.trim)
        (name, value)
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
        (req, params) <- parseRequestLine(lines.next());
        headers <- parseHeaders(lines.takeWhile({ s => s.contains(':') }));
        body <- getBody(lines)
        ) yield HttpRequest(req, headers.toMap, params, body)
    } catch {
      case ex:Throwable => println(ex); None
    }
  }

  def parseRequestLine(line: String): Option[(RequestLine, Map[String, String])] = {
    line match {
      case requestPattern(method, resource, version) => {
        val params = paramPattern.findAllIn(resource).matchData.map(_.subgroups).map(g => g(0) -> g(1))
        Some((RequestLine(method, resource.split("\\?")(0), version), params.toMap))
      }
      case _ => None
    }
  }

  def getParams(request: String): Option[(String, Map[String,String])] = {
    try {
      val parsedResource = request.trim.split("\\?", 2)
      if (parsedResource.size > 1 && parsedResource(1).size > 0) {
        val paramMap = parsedResource(1)
          .split('&')
          .map {x => x.split("=", 2)}
          .collect {case x => (x(0), x(1))}
          .toMap

        return Some((parsedResource(0), paramMap))
      }
      Some(parsedResource(0), Map.empty)
    } catch {
      case ex:Throwable => println(ex);None
    }
  }
}