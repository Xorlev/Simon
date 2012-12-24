package com.xorlev.simon.conf

import com.xorlev.simon.handlers.TestHandler
import com.xorlev.simon.request.RequestMapper

/**
 * 2012-12-10
 * @author Michael Rose <michael@fullcontact.com>
 */

class StaticDispatchConfig extends DispatchConfig {
  def config() {
    //RequestMapper.registerHandler("/", new StaticFileRequestHandler("/", "_site/"))
    RequestMapper.registerHandler("/", new TestHandler("/"))
  }
}