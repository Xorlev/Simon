package com.xorlev.simon

import java.lang.String
import java.net.{SocketException, ServerSocket, InetAddress}
import util._
import java.util.concurrent.{TimeUnit, Executors}
import com.yammer.metrics.scala.Instrumented
import sun.misc.{Signal, SignalHandler}


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
    Signal.handle(new Signal("TERM"), new SignalHandler {
      def handle(p1: Signal) {
        stopServer()
      }
    })
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

        tp.submit(new ConnectionHandler(sock))
        //new Handler(sock).run()
      }
    } catch {
      case e:SocketException => log.error("Socket error:",e)
    }

  }

  def stopServer() = {
    log.info("Shutting down...")
    running = false
    tp.awaitTermination(5, TimeUnit.SECONDS)
    tp.shutdown()
    log.info("Closing listener")
    socket.close()
    System.exit(0)
  }
}