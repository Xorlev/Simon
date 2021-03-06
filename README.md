Simon: Scala Webserver
======================

This project serves as a toy webserver for me to learn Scala. Subsequently, it may be full of language faux-pas,
overly-complex functional garbage, overly-simple OO-garbage, and text/plain garbage.

Over time, I hope this project will improve my ability to organize and scale functional code.

Disclaimer
----------
Quality is not assured by any means, nor is this meant as a production-grade webserver.

Currently multi-threaded, executor-based blocking I/O. Implements HTTP/1.0 only

The Challenge
-------------
Write a webserver from the ground up avoiding any existing HTTP-related code. Write this server in Scala.

Goals
-----
+ HTTP/1.1 support
+ Efficient parsing HTTP headers
+ Dealing with IO myself (not Netty)
+ Writing interesting request handlers (e.x. Sinatra-esque DSL, Scalate/Mustache/etc. view handlers)
+ Modules (e.x. Proxy, Config sources, ZK service discovery)
+ Servlet integration
+ Embeddable
+ Performant?
+ Extensible

License
-------
Copyright 2012 Michael Rose

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.