package com.xorlev.simon

import conf.StaticDispatchConfig
import handlers.{TestHandler, StaticFileRequestHandler}
import util.Loggable
import org.yaml.snakeyaml.Yaml
import java.io.{IOException, FileInputStream}


/**
 * 2012-11-25
 * @author Michael Rose <elementation@gmail.com>
 */

object ServerRunner extends Loggable {
  def main(args:Array[String]) {
    log.info("Starting Simon HTTP server")

    new HttpServer("localhost", 1337)
      .addDispatchConfig(new StaticDispatchConfig)
      .runServer()
  }
}