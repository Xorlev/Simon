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
    val lineMatcher = "(GET|POST|PUT|DELETE|OPTIONS|HEAD) (.*) HTTP/(1.0|1.1)".r
      .pattern
      .matcher(line)

    if (lineMatcher.matches) {
      val (method, resource, version) = (lineMatcher.group(1), lineMatcher.group(2), "HTTP/" + lineMatcher.group(3))
      val parsedParams = getParams(resource).get

      return Some((RequestLine(method, parsedParams._1, version), parsedParams._2))
    }

    None
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