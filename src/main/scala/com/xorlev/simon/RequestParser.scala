package com.xorlev.simon

import model.HttpRequest
import io.{BufferedSource, Source}
import util.Loggable
import collection.mutable.ListBuffer
import java.io.{InputStreamReader, BufferedReader, InputStream}
import java.nio.{CharBuffer, ByteBuffer}

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
        body <- getBody(headers, reader)
        ) yield HttpRequest(req, headers.toMap, params, body)
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
}