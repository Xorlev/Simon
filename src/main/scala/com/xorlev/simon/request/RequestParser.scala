package com.xorlev.simon.request

import io.{BufferedSource, Source}
import collection.mutable.ListBuffer
import java.io.{InputStreamReader, BufferedReader, InputStream}
import java.nio.{CharBuffer, ByteBuffer}
import com.xorlev.simon.util.{MimeUtil, Loggable}
import com.xorlev.simon.model.HttpRequest
import java.net.URLDecoder

/**
 * 2012-11-25
 * @author Michael Rose <elementation@gmail.com>
 */

object RequestParser extends Loggable {
  case class RequestLine(method: String, resource: String, version: String)
  case class HeaderLine(name: String, value: String)
  val requestPattern = "(GET|POST|PUT|DELETE|OPTIONS|HEAD) (.*) (HTTP/1.0|HTTP/1.1)".r
  val paramPattern = "[?&]([^=]+)=([^=&]+)".r
  val bodyParamPattern = "([^=]+)=([^=&]+)".r

  def parseHeaders(headers: Iterator[String]): Option[List[(String, String)]] = {
    try {
      Some(headers.map { line =>
        val Array(name, value) = line.split(":", 2).map(_.trim)
        (name, value)
      }.toList)
    } catch {
      case ex: Throwable => println(ex);None
    }
  }

  def getBody(headers: List[(String,String)], reader: BufferedReader): Option[String] = {
    val len = headers.find(_._1 == "Content-Length").map(_._2) match {
      case Some(x) => Integer.parseInt(x)
      case None => 0
    }

    val buf = CharBuffer.allocate(len)

    reader.read(buf)
    Some(buf.flip().toString)
  }

  def decodeRequest(stream: InputStream): Option[HttpRequest] = {
    log.debug("Decoding...")


    val reader = new BufferedReader(new InputStreamReader(stream))
    val lb = new ListBuffer[String]
    var read = true
    while(read) {
      val line = reader.readLine()

      if (line == "") {
        read = false
      } else {
        lb.append(line)
      }
    }

    try {
      for (
        (req, params) <- parseRequestLine(lb.head);
        headers <- parseHeaders(lb.drop(1).iterator);
        body <- getBody(headers, reader);
        bodyParams <- extractBodyParams(req, headers,body)
        ) yield HttpRequest(req, headers.toMap, params ++ bodyParams, body)
    } catch {
      case ex:Throwable => None
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


  def extractBodyParams(req: RequestLine, headers: List[(String, String)], body: String): Option[Map[String, String]] = req.method match {
    case "POST" | "PUT" => headers.find(_._1 == "Content-Type").map(_._2) match {
      case Some(MimeUtil.FORM) => Some(bodyParamPattern.findAllIn(URLDecoder.decode(body, "UTF-8")).matchData.map(_.subgroups).map(g => g(0) -> g(1)).toMap)
      case None => Some(Map.empty)
    }
    case _ => Some(Map.empty)
  }
}