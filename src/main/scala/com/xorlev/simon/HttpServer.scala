package com.xorlev.simon

import handlers.StaticFileRequestHandler
import java.lang.String
import java.net.{Socket, ServerSocket, InetAddress}
import collection.mutable.ListBuffer
import io.Source
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import java.io.{ByteArrayOutputStream, OutputStream, ByteArrayInputStream}
import com.google.common.io.ByteStreams
import util.HeaderUtil
import java.util.zip.{DeflaterOutputStream, GZIPOutputStream}
import com.xorlev.simon.RequestParser.HttpRequest

/**
 * 2012-11-25
 * @author Michael Rose <michael@fullcontact.com>
 */

class HttpServer(host: String, port: Int) {
  val log = LoggerFactory.getLogger(this.getClass)
  val addr = InetAddress.getByName(host)
  val socket = new ServerSocket(port)

  var running: Boolean = false

  def runServer() = {
    running = true

    log.info("Server started on {}", port)
    while(running) {
      val sock = socket.accept()
      log.info("Accepted socket")

      handleRequest(sock)

      sock.close()
    }

  }

  def stop() = {
    running = false
    socket.close()
  }

  def handleRequest(sock: Socket) {
    val req = RequestParser.decodeRequest(Source.createBufferedSource(sock.getInputStream))
    log.info("Parsed request {}", req)

    val resp = req.flatMap { r =>
      getHandler(r.request.resource).handleRequest(r)
    }.getOrElse {
      HttpResponse(400, "text/html", new ByteArrayInputStream("<h2>Bad Request</h2>".getBytes))
    }

    val os = sock.getOutputStream
    writeContent(req.get, os, resp)
    os.flush()
    os.close()
  }

  def getHandler(path: String): RequestHandler = {
    new StaticFileRequestHandler("/Users/xorlev/Code/blog/_site/")
  }

  def writeContent(request: HttpRequest, os: OutputStream, response: HttpResponse) {
    val codes = Map(
      200 -> "200 OK",
      400 -> "400 Bad Request",
      404 -> "404 Not Found",
      500 -> "500 Internal Server Error"
    )
    var headers = ListBuffer(
      "HTTP/1.1 " + codes(response.responseCode),
      "Content-Type: " + response.mimeType +"",
      "Date: " + HeaderUtil.now(),
      "X-XSS-Protection: 1; mode=block",
      //"Content-Encoding: deflate",
      "Connection: close"
    )

    /*val x = <HTML>
      <HEAD>
        <TITLE>Hello World!</TITLE>
      </HEAD>
      <BODY>
        <H1>Hello World!</H1>
        The webserver is up and running
        <A HREF="http://www.google.com/">here</A>.
      </BODY>
     </HTML>*/

    val inputStream = response.response
    val x = new ByteArrayOutputStream()
    //val size = ByteStreams.copy(response.response, new DeflaterOutputStream(x))

    headers += "Content-length: " + inputStream.available

    os.write((headers.mkString("\r\n") + "\r\n\r\n").getBytes)

    os.write(ByteStreams.toByteArray(inputStream))
    os.flush()
    inputStream.close()
  }
}