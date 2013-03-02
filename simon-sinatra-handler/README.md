#Sinatra Handler

Implements a Sinatra-inspired request handler for the Simon HTTP server

Example usage:
``` scala
class TestHandler(basePath: String) extends DynamicMethodHandler {
  get("/") {
    <html>
      <head>
        <title>HelloWorld</title>
      </head>
      <body>
        <h1>Test</h1>
        {new Date()}
        {params.getOrElse("hello", "defaultWorld")}<br />
        <ul>
          {
            request.headers.map { xs =>
              <li>{xs}</li>
            }
          }
        </ul>
      </body>
    </html>
  }

  get("/dynamic/:id") {
    List(params("id"))
  }

  get("/json") {
    List(1,2,3,4)
  }


  post("/json") {
    if (params("hello") != "world") halt(400, "Testing Halt")
    List(1,2,3,4).reverse
  }
}
object TestHandler extends Loggable{
  def main(args:Array[String]) {
    log.info("Starting Simon HTTP server")

    new HttpServer("localhost", 1337)
      .addHandler("/", new TestHandler("/"))
      .runServer()
  }
}
```