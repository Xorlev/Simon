package com.xorlev.simon

import java.lang.String
import java.net.{SocketException, Socket, ServerSocket, InetAddress}
import collection.mutable.ListBuffer
import java.io.{InputStream, FileInputStream, OutputStream, ByteArrayInputStream}
import com.google.common.io.ByteStreams
import model.HttpResponse
import util._
import java.util.concurrent.{TimeUnit, Executors}
import com.yammer.metrics.scala.Instrumented
import sun.misc.{Signal, SignalHandler}

import java.nio.channels.Channels

/**
 * 2012-11-25
 * @author Michael Rose <elementation@gmail.com>
 */

class HttpServer(host: String, port: Int) extends Loggable with Instrumented {
  val addr = InetAddress.getByName(host)
  val socket = new ServerSocket(port, 16384)

  var running: Boolean = false

  val tp = Executors.newFixedThreadPool(16)
  val m = metrics.meter("requests", "requests")
  init()

  def init() {
    val h:SignalHandler = new SignalHandler {
      def handle(p1: Signal) {
        stop()
      }
    }
    Signal.handle(new Signal("TERM"), h)
    /*Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      def run() { stop() }
    }))*/
  }

  def runServer() {
    running = true
    socket.setPerformancePreferences(0,1,2)
    socket.setReuseAddress(true)

    log.info("Server started on {}, ({})", port, JmxUtil.getPid)
    try {
      while(running) {
        val sock = socket.accept()
        sock.setReuseAddress(true)
        sock.setKeepAlive(false)
        m.mark()
        log.trace("Accepted socket {}", sock.getRemoteSocketAddress)

        tp.submit(new Handler(sock))
        //new Handler(sock).run()
      }
    } catch {
      case e:SocketException => log.error("Socket error:",e)
    }

  }

  def stop() = {
    log.info("Shutting down...")
    running = false
    tp.awaitTermination(5, TimeUnit.SECONDS)
    tp.shutdown()
    log.info("Closing listener")
    socket.close()
    System.exit(0)
  }

  class Handler(socket: Socket) extends Runnable with Instrumented {
    def run() {
      handleRequest(socket)
    }

    def handleRequest(sock: Socket) {
      sock.setSoTimeout(3000)
      val req = RequestParser.decodeRequest(sock.getInputStream)
      log.info("Parsed request {}", req)

      val resp = try {
        req.flatMap {
          r =>
            RequestMapper.getHandler(r.request.resource).handleRequest(r)
        }.getOrElse {
          HttpResponse(400, "text/html", new ByteArrayInputStream("<h2>Bad Request</h2>".getBytes))
        }
      } catch {
        case ex:Throwable => HttpResponse(500, "text/html", new ByteArrayInputStream(RenderUtil.renderStackTrace(ex).getBytes))
      }

      log.debug("Response: {}", resp)
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
        log.info("Parsed request {}", req)

        val resp = try {
          req.flatMap {
            r =>
              RequestMapper.getHandler(r.request.resource).handleRequest(r)
          }.getOrElse {
            HttpResponse(400, "text/html", new ByteArrayInputStream("<h2>Bad Request</h2>".getBytes))
          }
        } catch {
          case ex:Throwable => HttpResponse(500, "text/html", new ByteArrayInputStream(RenderUtil.renderStackTrace(ex).getBytes))
        }

        log.debug("Response: {}", resp)
        writeContent(os, resp)
        os.flush()

        //        if (req.get.headers("Keep-Alive")
      }
      os.close()
      sock.close()
    }


    def writeContent(outputStream: OutputStream, response: HttpResponse) {
      val inputStream = response.response
      val headers = ListBuffer(
        "HTTP/1.1 " + HttpCodeUtil.lookupCode(response.responseCode),
        "Content-Type: " + response.mimeType +"",
        "Content-length: " + inputStream.available,
        "Date: " + HeaderUtil.now(),
        "Server: Simon/" + VersionUtil.getVersionString
      )
      headers.appendAll(response.extraHeaders.map{ it => it.name + ": " + it.value})
      headers.append("Connection: close")

      outputStream.write((headers.mkString("\r\n") + "\r\n\r\n").getBytes)

      ByteStreams.copy(inputStream, outputStream)
      outputStream.flush()
      inputStream.close()
    }

    def processInputStream(in: InputStream, out: OutputStream) {
      in match {
        case in:FileInputStream => {
          val ch = in.getChannel
          ch.transferTo(0, ch.size, Channels.newChannel(out))
        }
        case in:InputStream => out.write(ByteStreams.toByteArray(in))
      }
    }
  }
}