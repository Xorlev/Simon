package com.xorlev.simon

import java.net.Socket
import com.yammer.metrics.scala.Instrumented
import model.{HttpRequest, HttpResponse}
import util._
import java.io.{InputStream, FileInputStream, OutputStream, ByteArrayInputStream}
import collection.mutable.ListBuffer
import java.nio.channels.Channels
import com.google.common.io.ByteStreams
import java.util.UUID

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
          RequestMapper.getHandler(r.request.resource).handleRequest(r)
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
}