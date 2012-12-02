package com.xorlev.simon

import handlers.StaticFileRequestHandler
import util.Loggable
import org.yaml.snakeyaml.Yaml
import java.io.{IOException, FileInputStream}


/**
 * 2012-11-25
 * @author Michael Rose <elementation@gmail.com>
 */

object ServerRunner extends Loggable {
  def main(args:Array[String]) = {
    log.info("Starting Simon HTTP server")

    RequestMapper.registerHandler("/", new StaticFileRequestHandler("/", "/Users/xorlev/Code/blog/_site/"))

    new HttpServer("localhost", 1337)
      .runServer()
  }
}