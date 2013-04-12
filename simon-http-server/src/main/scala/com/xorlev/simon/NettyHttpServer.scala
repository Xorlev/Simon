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

import java.lang.String
import java.net._
import request.{StaticRequestMapper, RequestHandler}
import util._
import com.yammer.metrics.scala.Instrumented
import sun.misc.{Signal, SignalHandler}
import org.jboss.netty.channel._
import org.jboss.netty.buffer.{ChannelBuffers, ChannelBuffer}

/**
 * 2012-11-25
 * @author Michael Rose <elementation@gmail.com>
 */

class NettyHttpServer(host: String, port: Int) extends SimpleChannelHandler with Loggable with Instrumented {
  val addr = InetAddress.getByName(host)
  var running: Boolean = false

  val m = metrics.meter("requests", "requests")
  val requestMapper = StaticRequestMapper

  init()

  def init() {
    Signal.handle(new Signal("TERM"), new SignalHandler {
      def handle(p1: Signal) {
        stopServer()
      }
    })

  }

  def addHandler(path: String, handler: RequestHandler) = {
    requestMapper.registerHandler(path, handler)
    this
  }


  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
    val ch = ctx.getChannel
    val buf = e.getMessage.asInstanceOf[ChannelBuffer]

//    while(buf.readable()) {
//      print(buf.readByte().asInstanceOf[Char])
//    }
//    println()
    buf.discardReadBytes()

    val b = ChannelBuffers.buffer(70)
    b.writeBytes("HTTP/1.1 200 OK\r\nContent-length: 5\r\nConnection: close\r\n\r\nHello".getBytes)

    ch.write(b).addListener(new ChannelFutureListener {
      def operationComplete(p1: ChannelFuture) {
        p1.getChannel.close()
      }
    })
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) {
    log.error("Exception {}", e.getCause.getMessage, e.getCause)


    ctx.getChannel.close()
  }

  def runServer() {

  }

  def acceptSocket(serverSocket: ServerSocket):Socket = {
    val sock = serverSocket.accept()
    //log.trace("Accepted socket {}", sock.getRemoteSocketAddress)
    //m.mark()

    sock
  }

  def stopServer() {
    log.info("Shutting down...")
    running = false
//
//    tp.awaitTermination(1, TimeUnit.SECONDS)
//    tp.shutdown()

    log.info("Closing listener")
//    serverSocket.close()

    System.exit(0)
  }
}