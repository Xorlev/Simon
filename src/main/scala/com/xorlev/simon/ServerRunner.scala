package com.xorlev.simon


/**
 * 2012-11-25
 * @author Michael Rose <michael@fullcontact.com>
 */

object ServerRunner {
  def main(args:Array[String]) = {
    new HttpServer("localhost", 1337).runServer()
  }
}