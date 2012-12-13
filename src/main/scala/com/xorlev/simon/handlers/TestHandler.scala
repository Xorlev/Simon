package com.xorlev.simon.handlers

import java.util.Date

/**
 * 2012-12-02
 * @author Michael Rose <elementation@gmail.com>
 */

class TestHandler(basePath: String) extends DynamicMethodHandler {
  get("/") {
    <html>
      <head>
        <title>HelloWorld</title>
      </head>
      <body>
        <h1>Test</h1>
        {new Date()}
        {params("hello")}<br />
        {request.headers}
      </body>
    </html>
  }

  get("/json") {
    List(1,2,3,4)
  }


  post("/json") {
    List(1,2,3,4).reverse
  }
}