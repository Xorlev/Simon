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

import conf.DispatchConfig
import java.lang.String
import java.net.{Socket, SocketException, ServerSocket, InetAddress}
import request.{StaticRequestMapper, RequestHandler}
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
  val serverSocket = new ServerSocket(port, 16384)
  var running: Boolean = false
  val tp = Executors.newFixedThreadPool(16)
  val m = metrics.meter("requests", "requests")
  val requestMapper = StaticRequestMapper

  init()

  def init() {
    Signal.handle(new Signal("TERM"), new SignalHandler {
      def handle(p1: Signal) {
        stopServer()
      }
    })

    serverSocket.setPerformancePreferences(0,1,2)
    serverSocket.setReuseAddress(true)
  }

  def addHandler(path: String, handler: RequestHandler) = {
    requestMapper.registerHandler(path, handler)
    this
  }

  def runServer() {
    running = true

    log.info("Server started on {}, ({})", port, JmxUtil.getPid)
    try {
      while(running) {
        val sock = acceptSocket(serverSocket)

        tp.submit(new SocketConnectionHandler(sock, requestMapper))
      }
    } catch {
      case e:SocketException => log.error("Socket error:",e)
    }

  }

  def acceptSocket(serverSocket: ServerSocket):Socket = {
    val sock = serverSocket.accept()
    log.trace("Accepted socket {}", sock.getRemoteSocketAddress)
    m.mark()

    sock
  }

  def stopServer() {
    log.info("Shutting down...")
    running = false

    tp.awaitTermination(1, TimeUnit.SECONDS)
    tp.shutdown()

    log.info("Closing listener")
    serverSocket.close()

    System.exit(0)
  }
}