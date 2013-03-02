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

package com.xorlev.simon

import java.net.Socket
import com.yammer.metrics.scala.Instrumented
import model.{HttpRequest, HttpResponse}
import request.{RegexRequestMapper, StaticRequestMapper, RequestParser, RequestMapper}
import util._
import java.io.{InputStream, FileInputStream, OutputStream, ByteArrayInputStream}
import collection.mutable.ListBuffer
import java.nio.channels.Channels
import com.google.common.io.ByteStreams
import java.util.UUID
import scala.util.Random

class SocketConnectionHandler(socket: Socket) extends Runnable with Instrumented with Loggable {
  val requestId = UUID.randomUUID().toString

  def run() {
    handleRequest(socket)
  }

  def handleRequest(sock: Socket) {
    val start = System.nanoTime()

    val req = RequestParser.decodeRequest(sock.getInputStream)
    log.info("({}) Parsed request {}", requestId, req)

    val resp = getResponse(req)

    log.debug("(" + requestId +") Response: {}, {} ms", resp, (System.nanoTime() - start) / 1000000)
    val os = sock.getOutputStream
    writeContent(os, resp)
    os.flush()
    os.close()
    sock.close()
  }

  def handleKeepAliveRequest(sock: Socket) {
    val is = sock.getInputStream
    val os = sock.getOutputStream
    sock.setSoTimeout(3000)
    while(is.available() != -1) {
      val req = RequestParser.decodeRequest(is)
      log.info("({}) Parsed request {}", requestId, req)

      val resp = getResponse(req)

      log.debug("({}) Response: {}", requestId, resp)
      writeContent(os, resp)
      os.flush()
    }
    os.close()
    sock.close()
  }

  def getResponse(req: Option[HttpRequest]): HttpResponse = {
    try {
      req.flatMap {
        r =>
          StaticRequestMapper.getHandler(r.request.resource).handleRequest(r)
      }.getOrElse {
        HttpResponse(400, "text/html", new ByteArrayInputStream("<h2>Bad Request</h2>".getBytes))
      }
    } catch {
      case ex:Throwable => HttpResponse(500, "text/html", new ByteArrayInputStream(RenderUtil.renderStackTrace(ex).getBytes))
    }
  }

  /**
   * Takes a response object and translates it into a fully-formed HTTP response.
   * Responsible for generating standard headers
   * @param outputStream
   * @param response
   */
  def writeContent(outputStream: OutputStream, response: HttpResponse) {
    val inputStream = response.response
    val headers = ListBuffer(
      "HTTP/1.1 " + HttpCodeUtil.lookupCode(response.responseCode),
      "Content-Type: " + response.mimeType +"",
      "Content-length: " + inputStream.available,
      "Date: " + HeaderUtil.now(),
      "Server: " + VersionUtil.getServerString
      //"Transfer-Encoding: chunked"
    )

    // Append all the headers the response needs.
    headers.appendAll(response.extraHeaders.map{ it => it.name + ": " + it.value})

    // We aren't Keep-Alive compatible yet, signal this.
    headers.append("Connection: close")

    outputStream.write((headers.mkString("\r\n") + "\r\n\r\n").getBytes)

    processInputStream(inputStream, outputStream)
    outputStream.flush()
    inputStream.close()
  }

  /**
   * If file input stream, uses the kernel to do a sendfile() instead of reading into JVM
   * Otherwise, uses efficient stream-to-stream copy
   * @param in
   * @param out
   */
  def processInputStream(in: InputStream, out: OutputStream) {
    in match {
      case in:FileInputStream => {
        val ch = in.getChannel
        ch.transferTo(0, ch.size, Channels.newChannel(out))
      }
      case in:InputStream => ByteStreams.copy(in, out)
    }
  }

  /**
   * Chunked output -- currently unused. Implements octet-based chunk encoding.
   * @param in
   * @param out
   */
  def chunkedOutput(in: InputStream, out: OutputStream) {
    while(in.available() > 0) {
      var size = Random.nextInt(11)
      if (size == 0) size = 1
      if (size > in.available()) size = in.available()
      println("Chunking " + size + " bytes")

      out.write((Integer.toHexString(size) + "\r\n").getBytes)

      1 to size foreach { _ =>
        out.write(in.read())
      }

      out.write("\r\n".getBytes)
    }


    out.write("0\r\n\r\n".getBytes)
  }
}