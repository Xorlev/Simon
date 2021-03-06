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

package com.xorlev.simon.conf

//import com.xorlev.simon.handlers.TestHandler
import com.xorlev.simon.request.{StaticRequestMapper, RequestMapper}

/**
 * 2012-12-10
 * @author Michael Rose <michael@fullcontact.com>
 */

class StaticDispatchConfig extends DispatchConfig {
  def config() {
    //RequestMapper.registerHandler("/", new StaticFileRequestHandler("/", "_site/"))
   // StaticRequestMapper.registerHandler("/", new TestHandler("/"))
  }
}