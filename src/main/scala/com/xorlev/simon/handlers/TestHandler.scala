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
    if (params("hello") != "world") halt(400, "Testing Halt")
    List(1,2,3,4).reverse
  }
}